package com.rena21c.voiceorder.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
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
import com.rena21c.voiceorder.view.widgets.RecyclerViewEmptySupport;
import com.rena21c.voiceorder.view.widgets.TwoButtonDialogFragment;
import com.rena21c.voiceorder.viewholder.VendorInfoViewHolder;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


@SuppressWarnings("MissingPermission")
public class RecommendActivity extends HasTabActivity implements TwoButtonDialogFragment.TwoButtonDialogClickListener {

    public static final int REQUEST_CHECK_SETTINGS = 0x1;

    private static boolean located = false;
    private static double latitude;
    private static double longitude;

    private LocationManager.LocationUpdateListener locationUpdateListener;
    private LocationManager locationManager;

    private Retrofit retrofit;
    private ApiService apiService;
    private AppPreferenceManager appPreferenceManager;

    private VendorsRecyclerViewAdapter rvAdapter;
    private RecyclerViewEmptySupport rvVendors;

    private TextView tvCurrentLocation;
    private AppCompatAutoCompleteTextView actvSearch;

    private TwoButtonDialogFragment beforeCallDialog;

    private String vendorPhoneNumber;
    private int position;
    private LinearLayout llSearch;
    private View ibClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);

        appPreferenceManager = App.getApplication(getApplicationContext()).getPreferenceManager();

        rvAdapter = new VendorsRecyclerViewAdapter(appPreferenceManager, new VendorInfoViewHolder.CallButtonClickListener() {

            @Override public void onCallButtonClick(String phoneNumber, int itemPosition) {
                position = itemPosition;
                vendorPhoneNumber = phoneNumber;

                beforeCallDialog = TwoButtonDialogFragment.newInstance("‘거상앱으로 전화드립니다’라고 꼭 말씀해주세요", "취소", "통화");
                beforeCallDialog.show(getSupportFragmentManager(), "dialog");
            }
        });

        rvVendors = (RecyclerViewEmptySupport) findViewById(R.id.rvVendors);
        tvCurrentLocation = (TextView) findViewById(R.id.tvCurrentLocation);
        llSearch = (LinearLayout) findViewById(R.id.llSearch); // 검색시 포커스를 이동시키기 위한 뷰
        actvSearch = (AppCompatAutoCompleteTextView) findViewById(R.id.actvSearch);
        actvSearch.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                getSupportActionBar().hide();
            }
        });
        ibClose = findViewById(R.id.ibClose);
        actvSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    getSupportActionBar().hide();
                    ibClose.setVisibility(View.VISIBLE);
                } else {
                    getSupportActionBar().show();
                    ibClose.setVisibility(View.GONE);
                }
            }
        });
        actvSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!located) return;
                HashMap<String, Object> bodyMap = new HashMap<>();
                bodyMap.put("latitude", latitude);
                bodyMap.put("longitude", longitude);
                bodyMap.put("keyWord", s.toString());
                requestVendor(bodyMap);
            }

            @Override public void afterTextChanged(Editable s) {

            }
        });

        ibClose.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                llSearch.requestFocus();
                HashMap<String, Object> bodyMap = new HashMap<>();
                bodyMap.put("latitude", latitude);
                bodyMap.put("longitude", longitude);
                requestVendor(bodyMap);
                actvSearch.setText("");
            }
        });

        retrofit = App.getApplication(getApplicationContext()).getRetrofit();

        apiService = retrofit.create(ApiService.class);

        locationUpdateListener = new LocationManager.LocationUpdateListener() {

            @Override public void onLocationUpdateFailed(Status status) throws IntentSender.SendIntentException {
                status.startResolutionForResult(RecommendActivity.this, REQUEST_CHECK_SETTINGS);
            }

            @Override public void onLocationUpdated(double latitude, double longitude, String locality) {
                Log.d("LocationService,activit", latitude + " , " + longitude);
                located = true;

                tvCurrentLocation.setText(locality);

                HashMap<String, Object> bodyMap = new HashMap<>();
                bodyMap.put("latitude", latitude);
                bodyMap.put("longitude", longitude);

                RecommendActivity.latitude = latitude;
                RecommendActivity.longitude = longitude;

                requestVendor(bodyMap);
            }
        };

        Geocoder geocoder = new Geocoder(this);
        locationManager = new LocationManager(this, geocoder);
        locationManager.setLocationUpdateListener(locationUpdateListener);

        rvVendors.setLayoutManager(new LinearLayoutManager(this));
        rvVendors.setEmptyView(findViewById(R.id.tvEmptyView));
        rvVendors.addItemDecoration(new DividerItemDecoration(getApplicationContext(), R.drawable.shape_divider_recycler_view));
        rvVendors.setAdapter(rvAdapter);
    }

    private void requestVendor(HashMap<String, Object> bodyMap) {
        apiService
                .getNearbyVendors(bodyMap)
                .enqueue(new Callback<List<Vendor>>() {
                    @Override public void onResponse(Call<List<Vendor>> call, Response<List<Vendor>> response) {
                        if (response.body() != null) {
                            rvAdapter.setVendors(response.body());
                        } else {
                            rvAdapter.clearVendors();
                        }
                    }

                    @Override public void onFailure(Call<List<Vendor>> call, Throwable t) {
                        Log.d("LocationService", t.toString());
                    }
                });
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

    @Override public void onClickNegativeButton() {
        beforeCallDialog.dismiss();
    }

    @Override public void onClickPositiveButton() {
        beforeCallDialog.dismiss();

        appPreferenceManager.setCallTime(vendorPhoneNumber, System.currentTimeMillis());

        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + vendorPhoneNumber));
        startActivity(intent);

        ((VendorInfoViewHolder) rvVendors.findViewHolderForAdapterPosition(position)).bindElapsedTimeFromCall("방금전 통화");
    }

}
