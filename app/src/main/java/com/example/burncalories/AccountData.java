package com.example.burncalories;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class AccountData extends Account implements Comparable {
    private byte[] headshot;
    private String name;
    private int data;
    public AccountData(String name, byte[] headshot, int data){
        this.name = name;
        this.headshot = headshot;
        this.data = data;
    }

    @Override
    public int compareTo(Object o) {
        AccountData ad = (AccountData)o;
        return this.data - ad.data;
    }

    public AccountData(String name) {
        this.name = name;
    }

    public AccountData(){
        super();
    }

    public byte[] getHeadshot() {
        return headshot;
    }

    public void setHeadshot(byte[] headshot) {
        this.headshot = headshot;
    }

    public String getName() {
        return name;
    }

    public Bitmap getImage(){
        return BitmapFactory.decodeByteArray(headshot,0, headshot.length);
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getData() {
        return data;
    }

    public void setData(int data) {
        this.data = data;
    }
}
