package com.rena21c.voiceorder.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.rena21c.voiceorder.App;
import com.rena21c.voiceorder.R;
import com.rena21c.voiceorder.etc.AppPreferenceManager;
import com.rena21c.voiceorder.network.ApiService;
import com.rena21c.voiceorder.pojo.Vendor;
import com.rena21c.voiceorder.services.LocationManager;
import com.rena21c.voiceorder.view.DividerItemDecoration;
import com.rena21c.voiceorder.view.actionbar.TabActionBar;
import com.rena21c.voiceorder.view.adapters.VendorsRecyclerViewAdapter;
import com.rena21c.voiceorder.viewholder.VendorInfoViewHolder;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class RecommendActivity extends HasTabActivity {

    public static final int REQUEST_CHECK_SETTINGS = 0x1;

    private LocationManager.LocationUpdateListener listener;
    private LocationManager locationManager;

    private Retrofit retrofit;
    private ApiService apiService;

    private VendorsRecyclerViewAdapter adapter;

    private RecyclerView rvVendors;
    private TextView tvCurrentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);

        final AppPreferenceManager appPreferenceManager = App.getApplication(getApplicationContext()).getPreferenceManager();

        adapter = new VendorsRecyclerViewAdapter(appPreferenceManager, new VendorInfoViewHolder.CallButtonClickListener() {
            @Override public void onCallButtonClick(String phoneNumber) {

                appPreferenceManager.setCallTime(phoneNumber, System.currentTimeMillis());

                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + phoneNumber));
                startActivity(intent);
            }
        });

        rvVendors = (RecyclerView) findViewById(R.id.rvVendors);
        tvCurrentLocation = (TextView) findViewById(R.id.tvCurrentLocation);

        retrofit = App.getApplication(getApplicationContext()).getRetrofit();

        apiService = retrofit.create(ApiService.class);

        listener = new LocationManager.LocationUpdateListener() {
            @Override public void onLocationUpdateFailed(Status status) throws IntentSender.SendIntentException {
                status.startResolutionForResult(RecommendActivity.this, REQUEST_CHECK_SETTINGS);
            }

            @Override public void onLocationUpdated(double latitude, double longitude, String locality) {
                Log.d("LocationService,activit", latitude + " , " + longitude);

                tvCurrentLocation.setText(locality);

                HashMap<String, Double> bodyMap = new HashMap<>();
                bodyMap.put("latitude", latitude);
                bodyMap.put("longitude", longitude);

                apiService
                        .getNearbyVendors(bodyMap)
                        .enqueue(new Callback<List<Vendor>>() {
                            @Override public void onResponse(Call<List<Vendor>> call, Response<List<Vendor>> response) {
                                if(response.body() != null) adapter.setVendors(response.body());
                            }

                            @Override public void onFailure(Call<List<Vendor>> call, Throwable t) {
                                Log.d("LocationService", t.toString());
                            }
                        });
            }
        };

        Geocoder geocoder = new Geocoder(this);
        locationManager = new LocationManager(this, geocoder);
        locationManager.setLocationUpdateListener(listener);

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
