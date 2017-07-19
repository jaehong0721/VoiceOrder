package com.rena21c.voiceorder.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.rena21c.voiceorder.R;
import com.rena21c.voiceorder.view.widgets.OneButtonDialogFragment;

import java.util.Calendar;

public class RequestEstimateActivity extends HasTabActivity implements OneButtonDialogFragment.OneButtonDialogClickListener{

    private ImageView ivCallToRequestEstimate;

    private final int openTime = 9;
    private final int closeTime = 18;

    private OneButtonDialogFragment informOperationTimeDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_estimate);

        informOperationTimeDialog = OneButtonDialogFragment.newInstance("죄송합니다.\n견적 요청 운영시간은\n09:00 ~ 18:00 입니다.",
                                                                        "확인");

        ivCallToRequestEstimate = (ImageView) findViewById(R.id.ivCallToRequestEstimate);
        ivCallToRequestEstimate.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("MissingPermission")
            @Override public void onClick(View v) {

                Calendar currentDate = Calendar.getInstance();
                int currentHour = currentDate.get(Calendar.HOUR_OF_DAY);

                if(currentHour >= openTime && currentHour < closeTime) {
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + getResources().getString(R.string.grape_number)));
                    startActivity(intent);
                } else {
                    informOperationTimeDialog.show(getSupportFragmentManager(), "dialog");
                }
            }
        });
    }

    @Override public void onClickOkButton() {
        informOperationTimeDialog.dismiss();
    }
}
