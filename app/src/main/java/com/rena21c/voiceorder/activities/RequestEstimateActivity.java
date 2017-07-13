package com.rena21c.voiceorder.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.rena21c.voiceorder.R;

public class RequestEstimateActivity extends HasTabActivity {

    private ImageView ivCallToRequestEstimate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_estimate);

        ivCallToRequestEstimate = (ImageView) findViewById(R.id.ivCallToRequestEstimate);
        ivCallToRequestEstimate.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("MissingPermission")
            @Override public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + getResources().getString(R.string.grape_number)));
                startActivity(intent);
            }
        });
    }
}
