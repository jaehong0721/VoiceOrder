package com.rena21c.voiceorder.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.rena21c.voiceorder.App;
import com.rena21c.voiceorder.R;
import com.rena21c.voiceorder.etc.AppPreferenceManager;
import com.rena21c.voiceorder.firebase.FirebaseDbManager;
import com.rena21c.voiceorder.model.Estimate;
import com.rena21c.voiceorder.model.RequestedEstimateItem;
import com.rena21c.voiceorder.util.FileNameUtil;
import com.rena21c.voiceorder.view.actionbar.CloseActivityActionBar;
import com.rena21c.voiceorder.view.widgets.EstimateInputView;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;


public class InputEstimateActivity extends AppCompatActivity {

    private Button btnAddEstimateInputView;
    private Button btnRequestEstimate;
    private LinearLayout estimateInputViewContainer;

    private FirebaseDbManager dbManager;

    private String phoneNumber;
    private String restaurantName;
    private String restaurantAddress;

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

        AppPreferenceManager appPreferenceManager = App.getApplication(getApplicationContext()).getPreferenceManager();
        phoneNumber = appPreferenceManager.getPhoneNumber();

        dbManager = App.getApplication(getApplicationContext()).getDbMangaer();

        final CountDownLatch latch = new CountDownLatch(2);

        dbManager.getRestaurantName(phoneNumber, new ValueEventListener() {
            @Override public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    restaurantName = (String) dataSnapshot.getValue();
                } else {
                    restaurantName = phoneNumber;
                }
                latch.countDown();
            }

            @Override public void onCancelled(DatabaseError databaseError) {
                FirebaseCrash.report(databaseError.toException());
                latch.countDown();
            }
        });

        dbManager.getRestaurantAddress(phoneNumber, new ValueEventListener() {
            @Override public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                    restaurantAddress = (String) dataSnapshot.getValue();
                latch.countDown();
            }

            @Override public void onCancelled(DatabaseError databaseError) {
                FirebaseCrash.report(databaseError.toException());
                latch.countDown();
            }
        });

        estimateInputViewContainer = (LinearLayout) findViewById(R.id.estimate_input_view_container);

        btnAddEstimateInputView = (Button) findViewById(R.id.btnAddEstimateInputView);
        btnAddEstimateInputView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                estimateInputViewContainer.addView(new EstimateInputView(InputEstimateActivity.this));
            }
        });

        btnRequestEstimate = (Button) findViewById(R.id.btnRequestEstimate);
        btnRequestEstimate.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                ArrayList<RequestedEstimateItem> items = new ArrayList<>();

                for(int i = 0; i < estimateInputViewContainer.getChildCount(); i++) {
                    View view = estimateInputViewContainer.getChildAt(i);
                    if(!(view instanceof EstimateInputView)) continue;

                    String itemName = ((EstimateInputView) view).getItemName();
                    String itemNum = ((EstimateInputView) view).getItemNum();

                    if(itemName == null || itemNum == null) continue;

                    RequestedEstimateItem requestedEstimateItem = new RequestedEstimateItem();
                    requestedEstimateItem.itemName = itemName;
                    requestedEstimateItem.itemNum = itemNum;
                    items.add(requestedEstimateItem);
                }

                saveEstimate(items, latch);
                finish();
            }
        });
    }

    private void saveEstimate(final ArrayList<RequestedEstimateItem> items, final CountDownLatch latch) {
        if(items.size() == 0) return;

        new Thread(new Runnable() {
            @Override public void run() {
                try {
                    latch.await();

                    Estimate estimate = new Estimate();
                    estimate.timeMillis = System.currentTimeMillis();
                    estimate.restaurantName = restaurantName;
                    estimate.restaurantAddress = restaurantAddress;
                    estimate.items = items;

                    String estimateKey = FileNameUtil.makeFileName(phoneNumber, estimate.timeMillis);

                    dbManager.setEstimate(estimateKey, estimate);
                } catch (InterruptedException e) {
                    FirebaseCrash.report(e);
                }
            }
        }).start();
    }
}
