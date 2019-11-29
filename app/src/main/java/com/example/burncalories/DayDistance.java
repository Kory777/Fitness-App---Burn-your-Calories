package com.example.burncalories;

import org.litepal.crud.DataSupport;
/***
 * Record every day step for each account
 */

public class DayDistance extends DataSupport {
    private int distance;
    private String date;
    private String name;

    public DayDistance(int distance, String date, String accountName) {
        this.distance = distance;
        this.date = date;
        this.name = accountName;
    }

    public DayDistance(){
        super();
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAccountName() {
        return name;
    }

    public void setAccountName(String accountName) {
        this.name = accountName;
    }
}
