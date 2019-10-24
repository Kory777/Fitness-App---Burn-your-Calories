package com.example.burncalories;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.burncalories.step.StepCounterManager;
import com.example.burncalories.view.CardItem;
import com.example.burncalories.view.CardPagerAdapter;
import com.example.burncalories.view.ShadowTransformer;
import com.example.burncalories.view.StepArcView;

import java.util.Observable;
import java.util.Observer;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;

    private ViewPager mViewPager;
    private StepArcView mArcView;

    private CardPagerAdapter mCardAdapter;
    private ShadowTransformer mCardShadowTransformer;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_intake);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_setting);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);


        mTextMessage = (TextView) findViewById(R.id.textView);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        mCardAdapter = new CardPagerAdapter();
        mCardAdapter.addCardItem(new CardItem(R.string.title_1, R.string.text_1));
        mCardAdapter.addCardItem(new CardItem(R.string.title_2, R.string.text_1));
        mCardAdapter.addCardItem(new CardItem(R.string.title_3, R.string.text_1));
        mCardAdapter.addCardItem(new CardItem(R.string.title_4, R.string.text_1));


        mCardShadowTransformer = new ShadowTransformer(mViewPager, mCardAdapter);

        mViewPager.setAdapter(mCardAdapter);
        mViewPager.setPageTransformer(false, mCardShadowTransformer);
        mViewPager.setOffscreenPageLimit(3);
        mCardShadowTransformer.enableScaling(true);


    }

    private void init(){

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void register() {
        StepCounterManager.getInstance().addStepCounterObserver(new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                mTextMessage.setText("芯片实时获取步数: " + (float) arg );
            }
        });

        StepCounterManager.getInstance().register();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        StepCounterManager.getInstance().clearStepObserver();
        StepCounterManager.getInstance().unRegister();
    }

}
