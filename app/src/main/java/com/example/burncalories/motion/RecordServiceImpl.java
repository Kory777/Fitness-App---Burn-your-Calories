package com.example.burncalories.motion;

import android.content.Context;
import android.util.Log;

import com.amap.api.maps.model.LatLng;

public class RecordServiceImpl implements  RecordService{
    private Context mContext;

    public RecordServiceImpl(Context context) {
        this.mContext = context;
    }

    @Override
    public void recordSport(LatLng latLng, String location) {
        Log.d("RecordServiceImpl","保存定位数据 = " + latLng.latitude + ":" + latLng.longitude + "   " + location);
    }
}
