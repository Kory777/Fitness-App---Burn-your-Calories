package com.example.burncalories.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.burncalories.R;

public class SetPlanActivity extends AppCompatActivity {
    SharedPreferences data;
    EditText editIntake;
    EditText editRun;
    EditText editStep;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_plan);
        setFinishOnTouchOutside(false);

        editIntake = findViewById(R.id.cal);
        editRun = findViewById(R.id.run);
        editStep = findViewById(R.id.step);

        data  = getSharedPreferences("BodyData", MODE_PRIVATE);
        float planIntake = data.getFloat("planIntake", 0.0f);
        float planDistance = data.getFloat("planDistance", 0.0f);
        float planStep = data.getFloat("planStep", 0.0f);

        //If data is not set (=0.0f), then display hint.
        if(planIntake >= 1)
            editIntake.setText(String.valueOf(planIntake));
        if(planDistance >= 1)
            editRun.setText(String.valueOf(planDistance));
        if(planStep >= 1)
            editStep.setText(String.valueOf(planStep));

        ImageView save = findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float[] planData = inputData();
                SharedPreferences.Editor editor = data.edit();
                editor.putFloat("planIntake",planData[0]);
                editor.putFloat("planDistance", planData[1]);
                editor.putFloat("planStep", planData[2]);
                editor.apply();
                setResult(RESULT_OK);
                finish();
            }
        });

    }

    public float[] inputData(){
        Float intake = Float.valueOf(editIntake.getText().toString());
        Float run = Float.valueOf(editRun.getText().toString());
        Float step = Float.valueOf(editStep.getText().toString());
        return new float[]{intake, run, step};
    }
}
