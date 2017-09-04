package com.rena21c.voiceorder.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.rena21c.voiceorder.R;
import com.rena21c.voiceorder.view.actionbar.CloseActivityActionBar;
import com.rena21c.voiceorder.view.widgets.EstimateInputView;


public class InputEstimateActivity extends AppCompatActivity {

    private Button btnAddEstimateInputView;
    private LinearLayout estimateInputViewContainer;

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

        estimateInputViewContainer = (LinearLayout) findViewById(R.id.estimate_input_view_container);

        btnAddEstimateInputView = (Button) findViewById(R.id.btnAddEstimateInputView);
        btnAddEstimateInputView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                estimateInputViewContainer.addView(new EstimateInputView(InputEstimateActivity.this));
            }
        });
    }
}
