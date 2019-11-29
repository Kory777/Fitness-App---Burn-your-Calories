package com.example.burncalories.Map;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.model.LatLng;
import com.autonavi.aps.amapapi.model.AMapLocationServer;

import java.util.ArrayList;
import java.util.List;

public class LocationService extends Service implements AMapLocationListener, LocationSource {
    private ArrayList<AMapLocation> points;
    private ArrayList<LatLng> locations; //Used for draw
    private AMapLocationClient mLocationClient;
    private LocationBinder mBinder;
    private AMapLocationClientOption mLocationOption;
    private boolean isRecord = false;
    private static String TAG = "LocationService";

    public LocationService() {

    }

    @Override
    public void onCreate(){
        Log.e(TAG, "onCreate");
//        setUpMap();
        super.onCreate();
        mBinder = new LocationBinder();
        points = new ArrayList<AMapLocation>();
        locations = new ArrayList<LatLng>();
        startlocation();

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return mBinder;
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        Log.e(TAG, "onLocationChanged");
        if(aMapLocation != null){
            isRecord = true;
            LatLng mylocation = new LatLng(aMapLocation.getLatitude(),
                    aMapLocation.getLongitude());
            points.add(aMapLocation);
            locations.add(mylocation);

        }
    }

    private void startlocation() {
        Log.e(TAG, "startLocation");
        if (mLocationClient == null) {
            mLocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            // 设置定位监听
            mLocationClient.setLocationListener(this);
            // 设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);

            mLocationOption.setInterval(2000);

            mLocationOption.setSensorEnable(false);

            // 设置定位参数
            mLocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mLocationClient.startLocation();

            mLocationOption.setSensorEnable(false);

        }
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        Log.e(TAG, "activate");
//        mListener = onLocationChangedListener;
        startlocation();
    }

    @Override
    public void deactivate() {
        Log.e(TAG, "deactivate");
//        mListener = null;
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();

        }
        mLocationClient = null;
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
    }

    /**
     * 设置一些amap的属性
     */
//    private void setUpMap() {
//        mAMap.setLocationSource(this);// 设置定位监听
//        mAMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
//        mAMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
//        // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
//        mAMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
//    }

    //用于获取后台信息
    class LocationBinder extends Binder{
        public ArrayList<AMapLocation> getPoints(){
            return points;
        }

        public ArrayList<LatLng> getLocations(){
            return locations;
        }

        public boolean isRecord(){
            return isRecord;
        }
    }

}
