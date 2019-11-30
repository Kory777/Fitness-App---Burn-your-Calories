package com.example.burncalories.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.burncalories.R;
import com.example.burncalories.utils.CloudDbHelper;

import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private EditText username;
    private EditText password;
    private Button button;
    private Map<String, String> accounts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        button = findViewById(R.id.buttonLogin);

        new Thread(){
            @Override
            public void run() {
                super.run();
                CloudDbHelper cdbHelper = new CloudDbHelper();
                accounts = cdbHelper.queryAccountNames();
            }
        }.start();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String accountName = username.getText().toString();
                String accountPassword = password.getText().toString();
                if(accounts == null){
                    Toast.makeText(LoginActivity.this, "Please check Internet connnection", Toast.LENGTH_LONG).show();
                }else if(accounts.get(accountName)== null){
                    Toast.makeText(LoginActivity.this, "This account do not exists", Toast.LENGTH_LONG).show();
                }else if(!accounts.get(accountName).equals(accountPassword)){
                    Toast.makeText(LoginActivity.this, "User name or password is incorrect", Toast.LENGTH_LONG).show();
                }else {
                    Intent intent = new Intent(LoginActivity.this, SettingActivity.class);
                    intent.putExtra("accountName", accountName);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
    }
}
