package com.example.burncalories;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.litepal.crud.DataSupport;

public class Account extends DataSupport {
    private byte[] headshot;
    private String name;
    public Account(String name, byte[] headshot){
        this.name = name;
        this.headshot = headshot;
    }

    public Account(String name) {
        this.name = name;
    }

    public Account(){
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


//    public void setName(String name) {
//        this.name = name;
//    }
}
