package com.example.burncalories;

import org.litepal.crud.DataSupport;

/***
 * Record one Date's all step
 */

public class DayStep extends DataSupport {
    private String date;
    private int step;
    private String name;

    public DayStep(String date, int step, String accountName) {
        this.date = date;
        this.step = step;
        this.name = accountName;
    }

    public DayStep(){

    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public String getAccountName() {
        return name;
    }

    public void setAccountName(String accountName) {
        this.name = accountName;
    }
}