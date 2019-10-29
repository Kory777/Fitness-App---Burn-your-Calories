package com.example.burncalories.step;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.util.Observer;

public class StepCounterManager implements SensorEventListener {
    private static final int STEP_SENSOR = Sensor.TYPE_STEP_COUNTER;

    private static StepCounterManager instance;

    private final String TAG = "StepCounterManager";

    private SensorManager mSensorManager;
    private Sensor mStepCount;
    private StepCounterObservable mStepCounterObservable;

    private StepCounterManager(){
        mSensorManager = (SensorManager)GlobalConfig.getAppContext().getSystemService(Context.SENSOR_SERVICE);

        if(mSensorManager == null){
            Log.e(TAG, "StepCounterManager init error");
            return;
        }
        mStepCount = mSensorManager.getDefaultSensor(STEP_SENSOR);

        mStepCounterObservable = new StepCounterObservable();
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

        mSensorManager.registerListener(this, mStepCount, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
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

        setStepCount(event.values[0]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
