package com.rena21c.voiceorder.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.rena21c.voiceorder.R;
import com.rena21c.voiceorder.services.LocationManager;
import com.rena21c.voiceorder.view.DividerItemDecoration;
import com.rena21c.voiceorder.view.actionbar.TabActionBar;
import com.rena21c.voiceorder.view.adapters.VendorsRecyclerViewAdapter;


public class RecommendActivity extends HasTabActivity {

    public static final int REQUEST_CHECK_SETTINGS = 0x1;

    private LocationManager.LocationUpdateListener listener;
    private LocationManager locationManager;

    private RecyclerView rvVendors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);

        listener = new LocationManager.LocationUpdateListener() {
            @Override public void onLocationUpdateFailed(Status status) throws IntentSender.SendIntentException {
                status.startResolutionForResult(RecommendActivity.this, REQUEST_CHECK_SETTINGS);
            }

            @Override public void onLocationUpdated(double latitude, double longitude) {
                Log.d("LocationService", latitude + " , " + longitude);

            }
        };

        locationManager = new LocationManager(this);
        locationManager.setLocationUpdateListener(listener);

        VendorsRecyclerViewAdapter adapter = new VendorsRecyclerViewAdapter();

        rvVendors = (RecyclerView) findViewById(R.id.rvVendors);
        rvVendors.setLayoutManager(new LinearLayoutManager(this));
        rvVendors.addItemDecoration(new DividerItemDecoration(getApplicationContext(), R.drawable.shape_divider_recycler_view));
        rvVendors.setAdapter(adapter);
    }

    @Override protected void onStart() {
        Log.d("LocationService", "onStart");
        locationManager.connectGoogleApiClient();
        super.onStart();
    }

    @Override protected void onResume() {
        Log.d("LocationService", "onResume");
        locationManager.startLocationUpdates();
        super.onResume();
    }

    @Override protected void onPause() {
        Log.d("LocationService", "onPause");
        locationManager.stopLocationUpdates();
        super.onPause();
    }

    @Override protected void onStop() {
        Log.d("LocationService", "onStop");
        locationManager.disconnectGoogleApiClient();
        super.onStop();
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {

            case REQUEST_CHECK_SETTINGS:

                switch (resultCode) {

                    case Activity.RESULT_CANCELED:
                        Toast.makeText(this, "업체추천 기능을 사용하려면 '위치'를 활성화해야 합니다", Toast.LENGTH_SHORT).show();
                        super.moveTab(TabActionBar.Tab.VOICE_ORDER);
                        finish();
                        break;
                }

                break;
        }
    }
}
