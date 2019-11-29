package com.example.burncalories.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.burncalories.Account;
import com.example.burncalories.AccountData;
import com.example.burncalories.DayDistance;
import com.example.burncalories.DayStep;
import com.example.burncalories.Map.MapMainActivity;
import com.example.burncalories.R;
import com.example.burncalories.step.StepData;
import com.example.burncalories.utils.Util;
import com.example.burncalories.view.RankItem;
import com.example.burncalories.view.RankPagerAdapter;
import com.loonggg.weekcalendar.view.WeekCalendar;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class RankActivity extends AppCompatActivity {
    private RankItem rankStep;
    private RankItem rankDistance;
    private ViewPager mViewPager;
    private WeekCalendar weekCalendar;
    private RankPagerAdapter mRankAdapter;
    private List<String> accountNames;
    private List<Account> accounts;
    private List<AccountData> stepDataList;
    private List<AccountData> distanceDataList;
    private static final String TAG = "RankActivity";

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    Intent intent = new Intent(RankActivity.this, MapMainActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                case R.id.navigation_dashboard:
                    return true;
                case R.id.navigation_notifications:
                    Intent intent3 = new Intent(RankActivity.this, SettingActivity.class);
                    startActivity(intent3);
                    finish();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);
        accounts = Util.getExistAccounts();
        rankStep = new RankItem("Step", null);
        rankDistance = new RankItem("Distance", null);

        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        initData();

        mRankAdapter = new RankPagerAdapter();
        mRankAdapter.addRankItem(rankDistance);
        mRankAdapter.addRankItem(rankStep);


        mViewPager.setAdapter(mRankAdapter);

        weekCalendar = findViewById(R.id.week_calendar);
        weekCalendar.setOnDateClickListener(new WeekCalendar.OnDateClickListener() {
            @Override
            public void onDateClick(String time) {
                Toast.makeText(RankActivity.this, time, Toast.LENGTH_SHORT).show();
                Util.loadDataFromCloud(time);
                updateData(time);
            }
        });

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }

    private void initData(){
        String date = Util.getTodayDate();
        stepDataList = new ArrayList<>();
        distanceDataList = new ArrayList<>();
        for(Account account: accounts){
            int step = 0, distance = 0;
            if(!DataSupport.where("date = ? and name = ?", date, account.getName()).find(DayDistance.class).isEmpty()) {
                distance = DataSupport.where("date = ? and name = ?", date, account.getName()).find(DayDistance.class).get(0).getDistance();
            }
            if(!DataSupport.where("date = ? and name = ?", date, account.getName()).find(DayStep.class).isEmpty()){
                step = DataSupport.where("date = ? and name = ?", date, account.getName()).find(DayStep.class).get(0).getStep();
            }
            //目前的头像均统一
            AccountData stepData = new AccountData(account.getName(),account.getHeadshot() ,step);
            AccountData distanceData = new AccountData(account.getName(),account.getHeadshot(),distance);

            stepDataList.add(stepData);
            distanceDataList.add(distanceData);
        }
        rankStep.setData(stepDataList);
        rankDistance.setData(distanceDataList);
    }

    private void updateData(String date){
        stepDataList = new ArrayList<>();
        distanceDataList = new ArrayList<>();
        for(Account account: accounts){
            int step = 0, distance = 0;
            if(!DataSupport.where("date = ? and name = ?", date, account.getName()).find(DayDistance.class).isEmpty()) {
                distance = DataSupport.where("date = ? and name = ?", date, account.getName()).find(DayDistance.class).get(0).getDistance();
            }
            if(!DataSupport.where("date = ? and name = ?", date, account.getName()).find(DayStep.class).isEmpty()){
                step = DataSupport.where("date = ? and name = ?", date, account.getName()).find(DayStep.class).get(0).getStep();
            }
            //Refresh headshot
            Util.loadHeadShotFromCLoud(account.getName());
            //目前的头像均统一
            AccountData stepData = new AccountData(account.getName(),account.getHeadshot() ,step);
            AccountData distanceData = new AccountData(account.getName(),account.getHeadshot(),distance);

            stepDataList.add(stepData);
            distanceDataList.add(distanceData);
        }
        rankStep.setData(stepDataList);
        rankDistance.setData(distanceDataList);
        mRankAdapter.notifyDataSetChanged();
    }

}
