package com.example.burncalories.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.burncalories.Account;
import com.example.burncalories.Map.MapMainActivity;
import com.example.burncalories.R;
import com.example.burncalories.utils.CloudDbHelper;
import com.example.burncalories.utils.FileUtil;
import com.makeramen.roundedimageview.RoundedImageView;

import org.litepal.crud.DataSupport;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.burncalories.utils.FileUtil.getRealFilePathFromUri;

public class SettingActivity extends AppCompatActivity {
    private CardView bodyDataCard;
    private CardView planCard;
    private float planIntake;
    private float planDistance;
    private float planStep;
    private static final int COMPLETED = 0;
    SharedPreferences sp;
    private EditText editAccount;
    private EditText editPassword;
    private RoundedImageView headShot;
    private String accountName;
    private static final String TAG = "SettingActivity";
    private Account account;
    private ImageButton buttonRefresh;
    private TextView textName;
    private AlertDialog.Builder builder;
    private static final int LOGOUT = 666;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == COMPLETED){
                headShot.setImageBitmap(account.getImage());
            }
        }
    };

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    Intent intent1 = new Intent(SettingActivity.this, MapMainActivity.class);
                    startActivity(intent1);
                    return true;
                case R.id.navigation_dashboard:
                    Intent intent2 = new Intent(SettingActivity.this, MainActivity.class);
                    startActivity(intent2);
                    return true;
                case R.id.navigation_notifications:
                    return true;
                case R.id.navigation_friends:
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sp  = getSharedPreferences("BodyData", MODE_PRIVATE);
        planIntake = sp.getFloat("planIntake", 0.0f);
        planDistance = sp.getFloat("planDistance", 0.0f);
        planStep = sp.getFloat("planStep", 0.0f);
        float weight = sp.getFloat("weight", 0.0f);
        float bmi = sp.getFloat("bmi", 0.0f);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        bodyDataCard = findViewById(R.id.bodyData);
        planCard = findViewById(R.id.plan);

        TextView planIntakeView = planCard.findViewById(R.id.data1);
        TextView planDistanceView = planCard.findViewById(R.id.data2);
        TextView planStepView = planCard.findViewById(R.id.data3);

        TextView textWeight = bodyDataCard.findViewById(R.id.data);

        textWeight.setText(String.valueOf(weight));
        planIntakeView.setText( "     " + planIntake + " Cal");
        planDistanceView.setText("     "+ planDistance + " km");
        planStepView.setText("     " + planStep);

        //Account Name
        accountName = sp.getString("account", "local");
        //HeadShot
        headShot = findViewById(R.id.portrait);
        setHeadShot();

        ImageView bodyCardImage = bodyDataCard.findViewById(R.id.image);

        ImageView planCardImage = planCard.findViewById(R.id.image);

        planCardImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, SetPlanActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        bodyDataCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, SetBodyDataActivity.class);
                startActivityForResult(intent,1);
            }
        });

        Button create = findViewById(R.id.create_account);
        editAccount = findViewById(R.id.account);
        editPassword = findViewById(R.id.password);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CloudDbHelper cDbHelper = new CloudDbHelper();
                String password = editPassword.getText().toString();
                String account = editAccount.getText().toString();
                if(cDbHelper.insertAccount(account, password)){
                    Toast.makeText(SettingActivity.this, "Sign up successfully", Toast.LENGTH_LONG);
                }else {
                    Toast.makeText(SettingActivity.this, "The user already exists or the Internet condition is poor", Toast.LENGTH_LONG);
                }

            }
        });
        headShot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImageFromGallery();
            }
        });
        textName = findViewById(R.id.accountName);
        textName.setText(accountName);
        textName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder = new AlertDialog.Builder(SettingActivity.this).setTitle("Logout confirmation")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                changeAccount("local");
                                setHeadShot();
                                Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
                                startActivityForResult(intent, LOGOUT);
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                builder.show();
            }
        });

        buttonRefresh = findViewById(R.id.refresh);
        buttonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadHeadShotFromCLoud(accountName);
            }
        });

        new Thread(){
            @Override
            public void run() {
                super.run();
                CloudDbHelper cdbHelper = new CloudDbHelper();
                Map<String, String> map = cdbHelper.queryAccounts();
                for(String value: map.values()){
                    System.out.println(value);
                }
            }
        }.start();

    }

    /**
     * 开整换头像
     *
     */
    //请求相册
    private static final int REQUEST_PICK = 101;

    private void getImageFromGallery(){
        Intent intent = new Intent();

        if (Build.VERSION.SDK_INT < 19) {
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
        } else {
            intent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
        }
        startActivityForResult(intent, REQUEST_PICK);
    }

    //请求相机
    private static final int REQUEST_CAPTURE = 100;
    //请求截图
    private static final int REQUEST_CROP_PHOTO = 102;
    //请求访问外部存储
    private static final int READ_EXTERNAL_STORAGE_REQUEST_CODE = 103;
    //请求写入外部存储
    private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 104;
    //调用照相机返回图片文件


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
       if(requestCode == 1 && resultCode == RESULT_OK)
           updateData();
       if (data != null && requestCode == REQUEST_PICK){
            Uri selectedImage = data.getData();
            String[] filePathColumns = {MediaStore.Images.Media.DATA};
            Cursor c = getContentResolver().query(selectedImage, filePathColumns, null, null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePathColumns[0]);
            String imagePath = c.getString(columnIndex);
            File file = new File(imagePath);
            String path = file.getAbsolutePath();
           try {
               //bitmap通过uri加载文件
               Bitmap bit = BitmapFactory.decodeStream(this.getContentResolver().openInputStream(selectedImage));
               Bitmap bitmap = resizeImage(bit, 500, 500);
               headShot.setImageBitmap(bitmap);
               if(account == null){
                   Log.e(TAG, "account = null 创建" + " name:" + accountName);
                   account = new Account(accountName, getByteOfBitMap(bitmap));
                   account.save();
                   saveHeadShotInCloud(accountName, getByteOfBitMap(bitmap));
                   Log.e(TAG, "保存完毕");
               }else {
                   Log.e(TAG, "account "+ account.getName() + " 更新");
                   account.setHeadshot(getByteOfBitMap(bitmap));
                   account.updateAll("name = ?", accountName);
                   saveHeadShotInCloud(accountName, getByteOfBitMap(bitmap));
               }
           } catch (FileNotFoundException e) {
               e.printStackTrace();
           }

        }else if(data != null && requestCode == LOGOUT){
           String str = data.getStringExtra("accountName");
           changeAccount(str);
           loadHeadShotFromCLoud(str);
           setHeadShot();
           textName.setText(accountName);
       }

    }



    public void updateData(){
        sp  = getSharedPreferences("BodyData", MODE_PRIVATE);
        planIntake = sp.getFloat("planIntake", 0.0f);
        planDistance = sp.getFloat("planDistance", 0.0f);
        planStep = sp.getFloat("planStep", 0.0f);
        float weight = sp.getFloat("weight", 0.0f);
        float bmi = sp.getFloat("bmi", 0.0f);
        TextView planIntakeView = planCard.findViewById(R.id.data1);
        TextView planDistanceView = planCard.findViewById(R.id.data2);
        TextView planStepView = planCard.findViewById(R.id.data3);
        TextView textWeight = bodyDataCard.findViewById(R.id.data);

        planIntakeView.setText( "     " + planIntake + " Cal");
        planDistanceView.setText("     "+ planDistance + " km");
        planStepView.setText("     " + planStep);
        textWeight.setText(String.valueOf(weight));
    }

    public Bitmap resizeImage(Bitmap bitmap, int w, int h) {
        Bitmap BitmapOrg = bitmap;
        int width = BitmapOrg.getWidth();
        int height = BitmapOrg.getHeight();
        int newWidth = w;
        int newHeight = h;

        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // if you want to rotate the Bitmap
        // matrix.postRotate(45);
        Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width,
                height, matrix, true);
        return resizedBitmap;
    }

    public byte[] getByteOfBitMap(Bitmap bitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] bitmapByte = stream.toByteArray();
        return bitmapByte;
    }

    /**
     * 设置头像控件
     */
    public void setHeadShot(){
        List<Account> accounts = DataSupport.where("name = ?", accountName).find(Account.class);
        if(!accounts.isEmpty()){
            account = accounts.get(0);
            Log.e(TAG, "account != null 获取头像");
            Bitmap headImage = account.getImage();
            headShot.setImageBitmap(headImage);
        }else {
            headShot.setImageResource(R.drawable.dog);
        }
    }

    public void changeAccount(String name){
        SharedPreferences.Editor editor = sp.edit();
        accountName = name;
        textName.setText(accountName);
        editor.putString("account", accountName);
        editor.apply();
    }

    public void saveHeadShotInCloud(String accountName, byte[] headshot){
        CloudDbHelper cDbHelper = new CloudDbHelper();
        cDbHelper.updateHeadShot(accountName, headshot);
    }

    //开线程
    public void loadHeadShotFromCLoud(String accountName){
        new Thread(){
            @Override
            public void run() {
                CloudDbHelper cloudDbHelper  = new CloudDbHelper();
                byte[] hs = cloudDbHelper.queryHeadShot(accountName);
                if(hs!=null) {
                    Log.e(TAG, "从云中加载");
                    Account newAccount= DataSupport.where("name = ?", accountName).findFirst(Account.class);
                    if(newAccount!=null) {
                        account = newAccount;
                        account.setHeadshot(hs);
                        account.updateAll("name = ?", accountName);
                    }else {
                        account = new Account(accountName);
                        account.setHeadshot(hs);
                        account.save();
                    }
                    Message msg = new Message();
                    msg.what = COMPLETED;
                    handler.sendMessage(msg);
                }
            }
        }.start();

    }
}