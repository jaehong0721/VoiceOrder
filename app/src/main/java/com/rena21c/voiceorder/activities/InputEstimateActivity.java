package com.rena21c.voiceorder.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.rena21c.voiceorder.R;
import com.rena21c.voiceorder.view.actionbar.CloseActivityActionBar;


public class InputEstimateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_estimate);

        CloseActivityActionBar.createWithActionBar(this, getSupportActionBar())
                .setBackButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                })
                .setTitle("견적요청");
    }
}
