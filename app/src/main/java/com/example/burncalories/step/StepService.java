package com.example.burncalories.step;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.burncalories.MyApplication;
import com.example.burncalories.utils.Util;

import org.litepal.crud.DataSupport;

import java.util.List;

public class StepService extends Service implements SensorEventListener {
    private SensorManager mSensorManager;
    private Sensor mStepCount;
    private static final int STEP_SENSOR = Sensor.TYPE_STEP_COUNTER;
    private int currentStep;
    private int previousStepCount;
    private static String CURRENT_DATE = "";
    private static final String TAG = "StepService";
    private static boolean isAlive = false;
    private int lastStep;
    SharedPreferences sp;
    /**
     * 每次第一次启动记步服务时是否从系统中获取了已有的步数记录
     */
    private boolean hasRecord;
    /**
     * Step at the time start Service
     */
    private int hasStep;

    private Thread monitorChangeDay;

    private Thread threadSave;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.e(TAG, "Start");
        super.onCreate();
        mSensorManager = (SensorManager)GlobalConfig.getAppContext().getSystemService(Context.SENSOR_SERVICE);
        mStepCount = mSensorManager.getDefaultSensor(STEP_SENSOR);
        mSensorManager.registerListener(this, mStepCount, SensorManager.SENSOR_DELAY_NORMAL);
        hasRecord = false;
        isAlive = true;
        initTodayData();


        /**
         * Monitor the change of date.
         */
        monitorChangeDay = new Thread(){
            @Override
            public void run() {
                while (isAlive) {
                    try {
                        //23h 57 minute 0s
                        long seconds = 23 * 60 * 60 * 1000 + 57 * 60 * 1000;
                        //Check date every one minute
                        if (!CURRENT_DATE.equals(Util.getTodayDate())) {
                            save();
                            Thread.sleep(seconds);
                        } else {
                            Thread.sleep(60000);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        /**
         * Save currentStep every 10 seconds
         */

//        sp = MyApplication.getContext().getSharedPreferences("Step",Context.MODE_PRIVATE);
//        threadSave =new Thread(){
//            @Override
//            public void run() {
//                super.run();
//                while (isAlive) {
//                    try {
//                        save();
//                        Thread.sleep(5 * 1000);
//                        SharedPreferences.Editor editor = sp.edit();
//                        editor.putInt("lastStep", lastStep);
//                        editor.apply();
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        };

        monitorChangeDay.start();



    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "Destory");
        super.onDestroy();
        mSensorManager.unregisterListener(this);
        isAlive = false;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() != STEP_SENSOR) {
            return;
        }

        int tempStep = (int) event.values[0];
        if(!hasRecord){
            hasRecord = false;
            hasStep = tempStep;
        }else {
            //获取APP打开到现在的总步数=本次系统回调的总步数-APP打开之前已有的步数
            int thisStepCount = tempStep - hasStep;
            //本次有效步数=（APP打开后所记录的总步数-上一次APP打开后所记录的总步数）
            int thisStep = thisStepCount - previousStepCount;
            //总步数=现有的步数+本次有效步数
            currentStep += (thisStep);
            //记录一次Service打开到现在的总步数
            previousStepCount = thisStepCount;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

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
            currentStep = Integer.valueOf(stepData.get(0).getStep());
        }
    }

    /**
     * 保存记步数据
     */
    public void save() {
        int tempStep = currentStep;
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

    public static boolean isAlive() {
        return isAlive;
    }
}
