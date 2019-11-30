package com.example.burncalories.step;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import com.example.burncalories.MyApplication;
import com.example.burncalories.activity.MainActivity;
import com.example.burncalories.utils.Util;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.util.List;
import java.util.Observer;

public class StepCounterManager implements SensorEventListener {
    private static final int STEP_SENSOR = Sensor.TYPE_STEP_COUNTER;

    private static StepCounterManager instance;

    private final String TAG = "StepCounterManager";

    private SensorManager mSensorManager;
    private Sensor mStepCount;
    private StepCounterObservable mStepCounterObservable;
    private static String CURRENT_DATE = "";
    private float currentStep = 0.0f;
    private int lastStep = 0;
    SharedPreferences sp;
    /**
     * 上一次的步数
     */
    private int previousStepCount = 0;
    /**
     * 系统中获取到的已有的步数
     */
    private int hasStepCount = 0;
    /**
     * 每次第一次启动记步服务时是否从系统中获取了已有的步数记录
     */
    private boolean hasRecord;

    private StepCounterManager(){
        mSensorManager = (SensorManager)GlobalConfig.getAppContext().getSystemService(Context.SENSOR_SERVICE);

        if(mSensorManager == null){
            Log.e(TAG, "StepCounterManager init error");
            return;
        }
        mStepCount = mSensorManager.getDefaultSensor(STEP_SENSOR);
        hasRecord = false;
        mStepCounterObservable = new StepCounterObservable();
        initTodayData();
    }

    private void initTodayData(){
        CURRENT_DATE = Util.getTodayDate();
        List<StepData> stepData =  DataSupport.where("date = ?", CURRENT_DATE).find(StepData.class);
        //New day
        if (stepData.size()==0 || stepData.isEmpty()){
            StepData newStepData = new StepData();
            newStepData.setDate(CURRENT_DATE);
            newStepData.setStep("0");
        }else if (stepData.size()==1){
            currentStep = Float.parseFloat(stepData.get(0).getStep());
        }
    }

    public static StepCounterManager getInstance(){
        if(instance == null){
            synchronized (StepCounterManager.class){
                if(instance==null){
                    instance = new StepCounterManager();
                }
            }
        }
        return instance;
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void register(){
        if(mStepCount == null){
            return;
        }
        String info = "name = "
                + mStepCount.getName() + ", version = " + mStepCount.getVersion() + ", vendor = " + mStepCount.getVendor()
                + ", FifoMaxEventCount = " + mStepCount.getFifoMaxEventCount()
                + ", FifoReservedEventCount = " + mStepCount.getFifoReservedEventCount() + ", MinDelay = "
                + mStepCount.getMinDelay() + ", MaximumRange = " + mStepCount.getMaximumRange()
                + ", Power = " + mStepCount.getPower()
                + ", ReportingMode = " + mStepCount.getReportingMode() + ", Resolution = " + mStepCount.getResolution() + ", MaxDelay = " + mStepCount.getMaxDelay();


        Log.i(TAG, "芯片信息 : " + info);

        mSensorManager.registerListener(this, mStepCount, SensorManager.SENSOR_DELAY_UI);
        sp = MyApplication.getContext().getSharedPreferences("Step",Context.MODE_PRIVATE);
        lastStep = sp.getInt("lastStep", 0);

         new Thread(){
            @Override
            public void run() {
                super.run();
                while (true) {
                    try {
                        save();
                        Thread.sleep(5 * 1000);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putInt("lastStep", lastStep);
                        editor.apply();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void flush() {
        mSensorManager.flush(this);
    }

    public void unRegister() {
        mSensorManager.unregisterListener(this);

    }

    public void addStepCounterObserver(Observer observer) {
        mStepCounterObservable.addObserver(observer);
    }

    private void setStepCount(float count) {
        mStepCounterObservable.sendChange(count);
    }

    public void clearStepObserver() {
        mStepCounterObservable.deleteObservers();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() != STEP_SENSOR) {
            return;
        }
        //获取当前传感器返回的临时步数
        int tempStep = (int) event.values[0];
        if (!hasRecord) {
            hasRecord = true;
            hasStepCount = tempStep;
            //Add when the app is dead;
            if(tempStep >= lastStep && lastStep != 0) {
                currentStep += tempStep - lastStep;
                Log.e("LastStep2", "  "+ lastStep);
            }
        } else {
            //获取APP打开到现在的总步数=本次系统回调的总步数-APP打开之前已有的步数
            int thisStepCount = tempStep - hasStepCount;
            //本次有效步数=（APP打开后所记录的总步数-上一次APP打开后所记录的总步数）
            int thisStep = thisStepCount - previousStepCount;
            //总步数=现有的步数+本次有效步数
            currentStep += (thisStep);
            //记录最后一次APP打开到现在的总步数
            previousStepCount = thisStepCount;
            Log.e("Test",  "hasStep: "+hasStepCount+ " thisStep:" + thisStep + " Current:" + currentStep);
        }

        if (mCallback != null) {
            mCallback.updateUi((int) currentStep);
        }

        lastStep = tempStep;

        //event.values[0] is the data from sensor
        setStepCount(event.values[0]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /**
     * 设置监听器对象
     */
    private UpdateUiCallBack mCallback;

    /**
     * 注册UI更新监听
     *
     * @param paramICallback
     */
    public void registerCallback(UpdateUiCallBack paramICallback) {
        this.mCallback = paramICallback;
    }



    /**
     * 保存记步数据
     */
    public void save() {
        int tempStep = (int) currentStep;
        List<StepData> list =  DataSupport.where("date = ?", CURRENT_DATE).find(StepData.class);
        Log.e(TAG, "List size:" +list.size());
        if (list.size() == 0 || list.isEmpty()) {
            StepData data = new StepData();
            data.setDate(CURRENT_DATE);
            data.setStep(tempStep + "");
            data.save();
        } else if (list.size() >= 1) {
            StepData data = list.get(0);
            data.setStep(tempStep + "");
            data.updateAll("date = ?", CURRENT_DATE);
        }
    }


}
