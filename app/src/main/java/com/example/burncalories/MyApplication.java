package com.example.burncalories;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.example.burncalories.step.GlobalConfig;

import org.litepal.LitePal;
import org.litepal.LitePalApplication;

public class MyApplication extends LitePalApplication {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        GlobalConfig.setAppContext(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LitePal.initialize(this);
    }
}
