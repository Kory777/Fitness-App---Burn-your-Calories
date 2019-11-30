package com.example.burncalories.step;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.util.Log;


public class StepDetectorManager implements SensorEventListener {
    private static final int STEP_DETECTOR = Sensor.TYPE_STEP_DETECTOR;

    private static StepDetectorManager instance;

    private SensorManager mSensorManager;

    private Sensor mStepDetect;

    private static final String TAG = "StepDetectorManager";


    private StepDetectorManager(){
        mSensorManager = (SensorManager) GlobalConfig.getAppContext().getSystemService(Context.SENSOR_SERVICE);
        if(mSensorManager == null){
            Log.e(TAG, "StepDetectorManager init error");
            return ;
        }
        mStepDetect = mSensorManager.getDefaultSensor(STEP_DETECTOR);
    }

    public static StepDetectorManager getInstance(){
        if(instance == null){
            synchronized (StepDetectorManager.class){
                if(instance == null){
                    instance = new StepDetectorManager();
                }
            }
        }
        return instance;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void flush() {
        mSensorManager.flush(this);
    }

    public void register(){
        if(mStepDetect == null){
            return;
        }
        mSensorManager.registerListener(this, mStepDetect, SensorManager.SENSOR_DELAY_UI);
    }

    public void unRegister() {
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() != STEP_DETECTOR) {
            return;
        }

        if (mCallback != null) {
            mCallback.updateUi(1);
        }
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
}
