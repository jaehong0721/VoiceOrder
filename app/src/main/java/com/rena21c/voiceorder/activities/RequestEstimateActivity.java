package com.rena21c.voiceorder.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.rena21c.voiceorder.R;
import com.rena21c.voiceorder.model.Estimate;

import java.util.ArrayList;

public class RequestEstimateActivity extends HasTabActivity {
    
    private static final int REQUEST = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_estimate);

        Button btnRequestEstimate = (Button) findViewById(R.id.btnRequestEstimate);

        btnRequestEstimate.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Intent intent = new Intent(RequestEstimateActivity.this, InputEstimateActivity.class);
                startActivityForResult(intent, REQUEST);
            }
        });
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST :
                if(resultCode == RESULT_OK) {
                    ArrayList<Estimate> items = (ArrayList)data.getSerializableExtra("items");

                    if(items == null) return;

                    setContentView(R.layout.activity_request_estimate_after);
                    Toast.makeText(this, items.toString(), Toast.LENGTH_SHORT).show();
                } else if(requestCode == RESULT_CANCELED){
                    Toast.makeText(this, "견적요청에 실패하였습니다. 잠시후에 다시 시도해주세요", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

}
