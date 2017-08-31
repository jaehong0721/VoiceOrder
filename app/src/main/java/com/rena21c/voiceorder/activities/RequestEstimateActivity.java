package com.rena21c.voiceorder.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.rena21c.voiceorder.R;

public class RequestEstimateActivity extends HasTabActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_estimate);

        Button btnRequestEstimate = (Button) findViewById(R.id.btnRequestEstimate);

        btnRequestEstimate.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Intent intent = new Intent(RequestEstimateActivity.this, InputEstimateActivity.class);
                startActivity(intent);
            }
        });
    }

}
