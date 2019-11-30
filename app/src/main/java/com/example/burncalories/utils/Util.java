package com.example.burncalories.utils;

import android.content.Context;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.model.LatLng;
import com.amap.api.trace.TraceLocation;
import com.example.burncalories.Account;
import com.example.burncalories.DayDistance;
import com.example.burncalories.DayStep;

import org.litepal.crud.DataSupport;

public class Util {

    public static Toast toast = null;
    /**
     * 将AMapLocation List 转为TraceLocation list
     *
     * @param list
     * @return
     */
    private static String TAG = "Util";
    public static List<TraceLocation> parseTraceLocationList(
            List<AMapLocation> list) {
        List<TraceLocation> traceList = new ArrayList<TraceLocation>();
        if (list == null) {
            return traceList;
        }
        for (int i = 0; i < list.size(); i++) {
            TraceLocation location = new TraceLocation();
            AMapLocation amapLocation = list.get(i);
            location.setBearing(amapLocation.getBearing());
            location.setLatitude(amapLocation.getLatitude());
            location.setLongitude(amapLocation.getLongitude());
            location.setSpeed(amapLocation.getSpeed());
            location.setTime(amapLocation.getTime());
            traceList.add(location);
        }
        return traceList;
    }
    public static TraceLocation parseTraceLocation(AMapLocation amapLocation) {
        TraceLocation location = new TraceLocation();
        location.setBearing(amapLocation.getBearing());
        location.setLatitude(amapLocation.getLatitude());
        location.setLongitude(amapLocation.getLongitude());
        location.setSpeed(amapLocation.getSpeed());
        location.setTime(amapLocation.getTime());
        return  location;
    }

    /**
     * 将AMapLocation List 转为LatLng list
     * @param list
     * @return
     */
    public static List<LatLng> parseLatLngList(List<AMapLocation> list) {
        List<LatLng> traceList = new ArrayList<LatLng>();
        if (list == null) {
            return traceList;
        }
        for (int i = 0; i < list.size(); i++) {
            AMapLocation loc = list.get(i);
            double lat = loc.getLatitude();
            double lng = loc.getLongitude();
            LatLng latlng = new LatLng(lat, lng);
            traceList.add(latlng);
        }
        return traceList;
    }

    public static AMapLocation parseLocation(String latLonStr) {
        if (latLonStr == null || latLonStr.equals("") || latLonStr.equals("[]")) {
            return null;
        }
        String[] loc = latLonStr.split(",");
        AMapLocation location = null;
        if (loc.length == 6) {
            location = new AMapLocation(loc[2]);
            location.setProvider(loc[2]);
            location.setLatitude(Double.parseDouble(loc[0]));
            location.setLongitude(Double.parseDouble(loc[1]));
            location.setTime(Long.parseLong(loc[3]));
            location.setSpeed(Float.parseFloat(loc[4]));
            location.setBearing(Float.parseFloat(loc[5]));
        }else if(loc.length == 2){
            location = new AMapLocation("gps");
            location.setLatitude(Double.parseDouble(loc[0]));
            location.setLongitude(Double.parseDouble(loc[1]));
        }

        return location;
    }

    public static ArrayList<AMapLocation> parseLocations(String latLonStr) {
        ArrayList<AMapLocation> locations = new ArrayList<AMapLocation>();
        String[] latLonStrs = latLonStr.split(";");
        for (int i = 0; i < latLonStrs.length; i++) {
            AMapLocation location = Util.parseLocation(latLonStrs[i]);
            if (location != null) {
                locations.add(location);
            }
        }
        return locations;
    }

    public static float calculationCalorie(float weight, float distance) {
        return (float) (weight * distance * 1.036);
    }

    /**
     * 获取当天日期
     *
     * @return
     */
    public static String getTodayDate() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }

    /**
     * 将时间转换成日期
     *
     * @return
     */
    public static String parseTimeToDate(String date){
        String []arr = date.split(" ");
        return arr[0];
    }

    /**
     * 开线程从云端加载
     * @param date
     */
    public static void loadDataFromCloud(String date){
        new Thread(){
            @Override
            public void run() {
                CloudDbHelper cloudDbHelper  = new CloudDbHelper();
                List<DayStep> steps = cloudDbHelper.queryUserStepByDate(date);
                List<DayDistance> distances = cloudDbHelper.queryUserDitanceByDate(date);
                storeCloudInLocal(steps, distances, date);

            }
        }.start();
    }

    /**
     *将云端数据储存到本地
     * @param steps
     * @param distances
     * @param date
     */
    private static void storeCloudInLocal(List<DayStep> steps, List<DayDistance> distances, String date){
        for(DayStep dayStep: steps){
            if(DataSupport.where("date = ? and name = ?", date, dayStep.getAccountName()).find(DayStep.class).isEmpty()){
                dayStep.save();
            }else {
                dayStep.updateAll("date = ? and name = ?", date, dayStep.getAccountName());
            }
        }

        for(DayDistance dayDistance: distances){
            if(DataSupport.where("date = ? and name = ?", date,dayDistance.getAccountName()).find(DayDistance.class).isEmpty()){
                dayDistance.save();
            }else {
                dayDistance.updateAll("date = ? and name = ?", date, dayDistance.getAccountName());
            }
        }
        Log.e(TAG,"云端数据加载成功");
    }


//    public static List<String> getExistAccountNames(){
//        List<Account> accounts = DataSupport.findAll(Account.class);
//        List<String> accountNames = new ArrayList();
//        for(Account account: accounts){
//            accountNames.add(account.getName());
//        }
//        return accountNames;
//    }

    public static List<Account> getExistAccounts(){
        List<Account> accounts = DataSupport.where("name <> ?", "").find(Account.class);
        ListIterator<Account> iterator = accounts.listIterator();
        while(iterator.hasNext()){
            Account account = iterator.next();
            if(account.getHeadshot()==null){
                iterator.remove();
            }
        }
        return accounts;
    }

    public static void loadAccountsFromCloud(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                CloudDbHelper cloudDbHelper = new CloudDbHelper();
                List<Account> accounts = cloudDbHelper.queryAccounts();
                Log.e(TAG, "LoadAccountSuccessfully");
                for(Account account: accounts){

                    if(DataSupport.where("name = ?", account.getName()).findFirst(Account.class)==null){
                        account.save();
                    }else {
                        account.updateAll("name = ?", account.getName());
                    }
                }
            }
        }.start();
    }

    /**
     * 从云端加载头像
     * @param accountName
     */
    public static void loadHeadShotFromCLoud(String accountName) {
        new Thread() {
            @Override
            public void run() {
                CloudDbHelper cloudDbHelper = new CloudDbHelper();
                Account account = null;
                byte[] hs = cloudDbHelper.queryHeadShot(accountName);
                if (hs != null) {
                    Log.e(TAG, "从云中加载");
                    Account newAccount = DataSupport.where("name = ?", accountName).findFirst(Account.class);
                    if (newAccount != null) {
                        account = newAccount;
                        account.setHeadshot(hs);
                        account.updateAll("name = ?", accountName);
                    } else {
                        account = new Account(accountName);
                        account.setHeadshot(hs);
                        account.save();
                    }
                }
            }
        }.start();
    }

    public static void showToast(Context context, String text, boolean isLongLength) {
        int length;
        if (isLongLength) {
            length = Toast.LENGTH_LONG;
        } else {
            length = Toast.LENGTH_SHORT;
        }
        if (toast == null) {
            toast = Toast.makeText(context, text, length);
        } else {
            toast.setText(text);
            toast.setDuration(length);
        }
        toast.show();
    }

}
