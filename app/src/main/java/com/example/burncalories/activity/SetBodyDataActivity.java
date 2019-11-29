package com.example.burncalories.activity;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.burncalories.R;

public class SetBodyDataActivity extends AppCompatActivity {
    SharedPreferences data;
    EditText editHeight;
    EditText editWeight;
    TextView textViewBMI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_body_data);

        data  = getSharedPreferences("BodyData", MODE_PRIVATE);
        float height = data.getFloat("height", 0.0f);
        float weight = data.getFloat("weight", 0.0f);
        float bmi = data.getFloat("bmi", 0.0f);

        editHeight = findViewById(R.id.height);
        editWeight = findViewById(R.id.weight);
        textViewBMI = findViewById(R.id.bmi);

        //If data is not set (=0.0f), then display hint.
        if(height >= 1)
            editHeight.setText(String.valueOf(height));
        if(weight >= 1)
            editWeight.setText(String.valueOf(weight));
        if(bmi >= 1)
            textViewBMI.setText(String.valueOf(bmi));

        ImageView save = findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float[] planData = inputData();
                SharedPreferences.Editor editor = data.edit();
                editor.putFloat("weight",planData[0]);
                editor.putFloat("height", planData[1]);
                editor.putFloat("bmi", planData[2]);
                editor.apply();
                setResult(RESULT_OK);
                finish();
            }
        });

    }

    public float[] inputData(){
        Float weight = Float.valueOf(editWeight.getText().toString());
        Float height = Float.valueOf(editHeight.getText().toString());
        Float bmi = weight / (float)Math.pow(height/100,2);
        return new float[]{weight, height, bmi};
    }
}
