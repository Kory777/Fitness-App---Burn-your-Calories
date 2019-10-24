package com.example.burncalories.step;

import android.content.Context;

public class GlobalConfig {
    private static Context sContext;

    public static void setAppContext(Context context) {
        sContext = context;
    }

    public static Context getAppContext() {
        return sContext;
    }
}
