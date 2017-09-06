package com.rena21c.voiceorder.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.rena21c.voiceorder.App;
import com.rena21c.voiceorder.R;
import com.rena21c.voiceorder.etc.AppPreferenceManager;
import com.rena21c.voiceorder.firebase.FirebaseDbManager;
import com.rena21c.voiceorder.model.Estimate;
import com.rena21c.voiceorder.util.TransformDataUtil;

public class RequestEstimateActivity extends HasTabActivity implements ChildEventListener{

    private static final int REQUEST = 0;
    private static final int MODIFY = 1;

    private FirebaseDbManager dbManager;
    private AppPreferenceManager appPreferenceManager;

    private Intent modifyIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_estimate);

        appPreferenceManager = App.getApplication(getApplicationContext()).getPreferenceManager();
        String phoneNumber = appPreferenceManager.getPhoneNumber();

        dbManager = App.getApplication(getApplicationContext()).getDbMangaer();
        dbManager.subscribeEstimate(phoneNumber, this);

        Button btnRequestEstimate = (Button) findViewById(R.id.btnRequestEstimate);

        btnRequestEstimate.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Intent intent = new Intent(RequestEstimateActivity.this, InputEstimateActivity.class);
                startActivityForResult(intent, REQUEST);
            }
        });

        modifyIntent = new Intent(RequestEstimateActivity.this, InputEstimateActivity.class);
    }

    @Override protected void onDestroy() {
        dbManager.cancelSubscriptionEstimate(this);
        super.onDestroy();
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST :
                if(resultCode != RESULT_OK)
                    Toast.makeText(this, "견적요청에 실패하였습니다. 잠시후에 다시 시도해주세요", Toast.LENGTH_SHORT).show();
                break;

            case MODIFY :
                if(resultCode != RESULT_OK)
                    Toast.makeText(this, "견적 요청 수정에 실패하였습니다. 잠시후에 다시 시도해주세요", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override public void onChildAdded(final DataSnapshot dataSnapshot, String s) {
        setContentView(R.layout.activity_request_estimate_after);

        Estimate estimate = dataSnapshot.getValue(Estimate.class);
        setDataFrom(estimate);
        setModifyIntentExtra(dataSnapshot.getKey(), estimate);

        Button btnModifyEstimate = (Button) findViewById(R.id.btnModifyEstimate);
        btnModifyEstimate.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                startActivityForResult(modifyIntent, MODIFY);
            }
        });
    }

    @Override public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        Estimate estimate = dataSnapshot.getValue(Estimate.class);
        setDataFrom(estimate);
        setModifyIntentExtra(dataSnapshot.getKey(), estimate);
    }

    @Override public void onChildRemoved(DataSnapshot dataSnapshot) {}

    @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

    @Override public void onCancelled(DatabaseError databaseError) {}

    private void setDataFrom(Estimate estimate) {
        String items = TransformDataUtil.makeRequestedEstimateItemsInLine(estimate.items);
        TextView tvItems = (TextView) findViewById(R.id.tvItems);
        tvItems.setText(items);
    }

    private void setModifyIntentExtra(String estimateKey, Estimate estimate) {
        modifyIntent.putExtra("modify", true);
        modifyIntent.putExtra("estimateKey", estimateKey);
        modifyIntent.putExtra("itemNames", estimate.makeItemNameList());
        modifyIntent.putExtra("itemNums", estimate.makeItemNumList());
    }
}
