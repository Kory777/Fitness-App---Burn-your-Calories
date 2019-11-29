package com.example.burncalories.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.burncalories.Account;
import com.example.burncalories.DayDistance;
import com.example.burncalories.DayStep;
import com.example.burncalories.Map.DbAdapter;
import com.example.burncalories.Map.MapMainActivity;
import com.example.burncalories.Map.PathRecord;
import com.example.burncalories.R;
import com.example.burncalories.step.StepCounterManager;
import com.example.burncalories.step.StepData;
import com.example.burncalories.step.UpdateUiCallBack;
import com.example.burncalories.utils.CloudDbHelper;
import com.example.burncalories.utils.Util;
import com.example.burncalories.view.CardItem;
import com.example.burncalories.view.CardPagerAdapter;
import com.example.burncalories.view.ShadowTransformer;
import com.example.burncalories.view.StepArcView;
import com.loonggg.weekcalendar.view.WeekCalendar;

import org.litepal.LitePal;
import org.litepal.crud.ClusterQuery;
import org.litepal.crud.DataSupport;

import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    public static boolean DEBUG_MODE = true;//是否是DEBUG模式
    private static MainActivity applicationContext;

    private ViewPager mViewPager;
    private StepArcView mArcView;
    private float currentStep;
//    private int distance;
    private CardItem itemStep;
    private CardItem itemDistance;
    private CardItem itemIntake;
    private int currentDistance;
    private String currentDate;
    TextView tvStep;
    private DbAdapter mDataBaseHelper;


    private WeekCalendar weekCalendar;
    private static String TAG = "MainACtivity";

    private CardPagerAdapter mCardAdapter;
    private ShadowTransformer mCardShadowTransformer;
    SharedPreferences sp;
    private String today;
    private int step;
    private Button buttonUpload;
    private String accountName;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    Intent intent = new Intent(MainActivity.this, MapMainActivity.class);
                    startActivity(intent);
                    finish();
                    mTextMessage.setText(R.string.title_intake);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_status);
                    return true;
                case R.id.navigation_notifications:
                    Intent intent3 = new Intent(MainActivity.this, SettingActivity.class);
                    startActivity(intent3);
                    mTextMessage.setText(R.string.title_settings);
                    finish();
                    return true;
                case R.id.navigation_friends:
                    Intent intent1 = new Intent(MainActivity.this, RankActivity.class);
                    startActivity(intent1);
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDataBaseHelper = new DbAdapter(this);
        mDataBaseHelper.open();
        //测试数据
       /* mDataBaseHelper.createrecord("100", "3.0 s", "3.0 m/s",
                "0.00000,0.0000,gps,1234567890,0.0,0.0,",
                "0.00000,0.0000,gps,1234567890,0.0,0.0,",
                "0.00000,0.0000,gps,1234567890,0.0,0.0,",
                "2019-11-08 12:00:00");
        mDataBaseHelper.createrecord("300", "3.0 s", "3.0 m/s",
                "0.00000,0.0000,gps,1234567890,0.0,0.0,",
                "0.00000,0.0000,gps,1234567890,0.0,0.0,",
                "0.00000,0.0000,gps,1234567890,0.0,0.0,",
                "2019-11-09 12:00:00");*/
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        weekCalendar = findViewById(R.id.week_calendar);
        currentDate = Util.getTodayDate();
        weekCalendar.setOnDateClickListener(new WeekCalendar.OnDateClickListener() {
            @Override
            public void onDateClick(String time) {
                int distance = getDistance(time);
                int step = getStep(time);
                currentDate = time;
                Toast.makeText(MainActivity.this, time, Toast.LENGTH_SHORT).show();
                itemStep.setCurrent(step);
                itemDistance.setCurrent(distance);
                mCardAdapter.notifyDataSetChanged();
            }
        });

        mTextMessage = (TextView) findViewById(R.id.textView);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        sp  = getSharedPreferences("BodyData", MODE_PRIVATE);
        float planIntake = sp.getFloat("planIntake", 0.0f);
        float planDistance = sp.getFloat("planDistance", 0.0f);
        float planStep = sp.getFloat("planStep", 0.0f);
        accountName = sp.getString("account", "local");

        today = Util.getTodayDate();



        mCardAdapter = new CardPagerAdapter();
        itemStep =  new CardItem( "", R.string.step, R.string.title_4);
        itemDistance = new CardItem("M", R.string.running_distance, R.string.title_4);
        itemIntake = new CardItem("Cal", R.string.cal_intake, R.string.title_4);

        currentDistance = getDistance(today);
        currentStep  = getStep(today);
        itemStep.setComplete(planStep);
        itemDistance.setComplete(planDistance);
        itemIntake.setComplete(planIntake);

        //Test
        itemStep.setCurrent(currentStep);
        itemDistance.setCurrent(currentDistance);
        itemIntake.setCurrent(50);

        mCardAdapter.addCardItem(itemStep);
        mCardAdapter.addCardItem(itemDistance);
        mCardAdapter.addCardItem(itemIntake);
        mCardAdapter.addCardItem(new CardItem("Test",R.string.title_4, R.string.step));
        mCardAdapter.notifyDataSetChanged();


        mCardShadowTransformer = new ShadowTransformer(mViewPager, mCardAdapter);

        mViewPager.setAdapter(mCardAdapter);
        mViewPager.setPageTransformer(false, mCardShadowTransformer);
        mViewPager.setOffscreenPageLimit(3);
        mCardShadowTransformer.enableScaling(true);
        Button button = findViewById(R.id.button);
        buttonUpload = findViewById(R.id.buttonUpload);

        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!accountName.equals("local")) {
                    StepCounterManager.getInstance().save();
                    uploadDataToCloud();
                }else {
                    Toast.makeText(MainActivity.this, "Please login to upload your data!"
                            , Toast.LENGTH_LONG).show();
                }
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemStep.setCurrent(itemStep.getCurrent()+1);
                mCardAdapter.notifyDataSetChanged();

            }
        });
        init();

    }

    private void init(){
        tvStep = findViewById(R.id.textView);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            register();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void register() {
        StepCounterManager.getInstance().addStepCounterObserver(new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                mTextMessage.setText("芯片实时获取步数: " + (float) arg);
                currentStep = (float)arg;
            }
        });

        StepCounterManager.getInstance().register();
        StepCounterManager.getInstance().registerCallback(new UpdateUiCallBack() {

            @Override
            public void updateUi(int stepCount) {
                //If the current day is today then refresh UI
                if(currentDate.equals(Util.getTodayDate())) {
                    itemStep.setCurrent(stepCount);
                    mCardAdapter.notifyDataSetChanged();
                }
            }

        });

    }

    @Override
    protected void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        StepCounterManager.getInstance().clearStepObserver();
        StepCounterManager.getInstance().unRegister();
    }

    @Override
    protected void onResume() {
        Log.e(TAG, "onResume");
        super.onResume();
    }
    @Override
    protected void onPause() {
        Log.e(TAG, "onPause");
        StepCounterManager.getInstance().save();
        super.onPause();

    }
    public void creatDatabase(){
        LitePal.getDatabase();
    }

    private int getDistance(String date){
        List<PathRecord> records = mDataBaseHelper.queryRecordByDate(date);
        int distance = 0;
        for(PathRecord record: records){
            distance += Float.parseFloat(record.getDistance());
        }
        DayDistance dd = DataSupport.where("date = ? and name = ?", date, accountName).findFirst(DayDistance.class);
        if(dd != null){
            dd.setDistance(distance);
            dd.updateAll("date = ? and name = ?", date, accountName);
        }else {
            dd = new DayDistance();
            dd.setDistance(distance);
            dd.setDate(date);
            dd.setAccountName(accountName);
            dd.save();
        }
        return distance;
    }

    private int getStep(String date){
        int step = 0;
        List<StepData> list = DataSupport.where("date = ?", date).find(StepData.class);
        if(!list.isEmpty()){
            StepData stepData = list.get(0);
            step = Integer.valueOf(stepData.getStep());
            DayStep ds = DataSupport.where("date = ? and name = ?", date, accountName).findFirst(DayStep.class);
            if(ds!=null){
                ds.setStep(step);
                ds.updateAll("date = ? and name = ?", date, accountName);
            }else{
                ds = new DayStep();
                ds.setStep(step);
                ds.setDate(date);
                ds.setAccountName(accountName);
                ds.save();
            }
        }
        return step;
    }

    /**
     * 更新当日数据至云端数据库
     */
    private void uploadDataToCloud(){
        new Thread(){
            @Override
            public void run() {
                if(!accountName.equals("local")){
                    CloudDbHelper cloudDbHelper = new CloudDbHelper();
                    cloudDbHelper.updateStepInCloud(accountName, currentDate, getStep(currentDate));
                    cloudDbHelper.updateDistanceInCloud(accountName, currentDate, getDistance(currentDate));
                }
            }
        }.start();
    }


}
