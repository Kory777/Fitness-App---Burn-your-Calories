package com.example.burncalories.step;

import android.support.annotation.NonNull;

import org.litepal.crud.DataSupport;

public class StepData extends DataSupport {
    private String date;
    private String step;

    public StepData(){
        super();
    }

    public String getDate() {
        return date;
    }

    public String getStep() {
        return step;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setStep(String step) {
        this.step = step;
    }

    @NonNull
    @Override
    public String toString() {
        return date + " 步数：" + step;
    }
}
