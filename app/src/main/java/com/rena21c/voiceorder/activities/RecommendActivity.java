package com.rena21c.voiceorder.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.firebase.crash.FirebaseCrash;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.rena21c.voiceorder.App;
import com.rena21c.voiceorder.R;
import com.rena21c.voiceorder.etc.AppPreferenceManager;
import com.rena21c.voiceorder.firebase.AnalyticsEventManager;
import com.rena21c.voiceorder.firebase.FirebaseDbManager;
import com.rena21c.voiceorder.firebase.HasDbListener;
import com.rena21c.voiceorder.network.ApiService;
import com.rena21c.voiceorder.pojo.Vendor;
import com.rena21c.voiceorder.services.LocationManager;
import com.rena21c.voiceorder.util.StringUtil;
import com.rena21c.voiceorder.view.DividerItemDecoration;
import com.rena21c.voiceorder.view.actionbar.TabActionBar;
import com.rena21c.voiceorder.view.adapters.VendorsRecyclerViewAdapter;
import com.rena21c.voiceorder.view.widgets.RecyclerViewEmptySupport;
import com.rena21c.voiceorder.view.widgets.TwoButtonDialogFragment;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;


@SuppressWarnings("MissingPermission")
public class RecommendActivity extends HasTabActivity implements TwoButtonDialogFragment.TwoButtonDialogClickListener {

    public static final int TEXT_INPUT_THRESHOLD = 500;

    public static final int REQUEST_CHECK_SETTINGS = 0;
    public static final int VENDOR_DETAIL= 1;

    private static boolean located = false;
    private static double latitude;
    private static double longitude;

    private LocationManager.LocationUpdateListener locationUpdateListener;
    private LocationManager locationManager;

    private Retrofit retrofit;
    private ApiService apiService;


    private FirebaseDbManager dbManager;
    private AppPreferenceManager appPreferenceManager;
    private AnalyticsEventManager eventManager;

    private RecyclerViewEmptySupport rvVendors;
    private VendorsRecyclerViewAdapter rvAdapter;

    private TextView tvCurrentLocation;
    private AppCompatAutoCompleteTextView actvSearch;

    private TwoButtonDialogFragment beforeCallDialog;

    private HashMap<String, String> calledVendors;

    private String restoPhoneNum;
    private String vendorPhoneNumber;
    private String vendorName;
    private int position;

    private RelativeLayout llSearch;
    private View ibClose;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);

        dbManager = App.getApplication(getApplicationContext()).getDbMangaer();
        appPreferenceManager = App.getApplication(getApplicationContext()).getPreferenceManager();
        eventManager = App.getApplication(getApplicationContext()).getEventManager();

        restoPhoneNum = appPreferenceManager.getPhoneNumber();
        calledVendors = appPreferenceManager.getCalledVendors();
        rvAdapter = new VendorsRecyclerViewAdapter(appPreferenceManager,
                new VendorsRecyclerViewAdapter.CallButtonClickListener() {
                    @Override public void onCallButtonClick(String phoneNumber, String name, int itemPosition) {
                        position = itemPosition;
                        vendorPhoneNumber = phoneNumber;
                        vendorName = name;

                        beforeCallDialog = TwoButtonDialogFragment.newInstance("‘거상앱으로 전화드립니다’\n라고 꼭 말씀해주세요", "취소", "통화");
                        beforeCallDialog.show(getSupportFragmentManager(), "dialog");
                    }
                },
                new VendorsRecyclerViewAdapter.AppDownloadListener() {
                    @Override public void onAppDownload() {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("market://details?id=com.rena21.driver"));
                        startActivity(intent);
                    }
                },
                new VendorsRecyclerViewAdapter.ClickVendorListener() {
                    @Override public void onClickVendor(final int itemPosition, final String phoneNumber, final String vendorName, final String vendorAddress,
                                                        final String majorItems, final View sharedView) {
                        dbManager.hasVendor(StringUtil.removeSpecialLetter(phoneNumber), new HasDbListener(RecommendActivity.this) {
                            @Override protected void hasDb() {
                                goToVendorDetail(itemPosition, phoneNumber,vendorName,vendorAddress,majorItems,sharedView, false);
                            }
                            @Override protected void hasNone() {
                                goToVendorDetail(itemPosition, phoneNumber,vendorName,vendorAddress,majorItems,sharedView, true);
                            }
                        });
                    }
                });

        rvVendors = (RecyclerViewEmptySupport) findViewById(R.id.rvVendors);
        tvCurrentLocation = (TextView) findViewById(R.id.tvCurrentLocation);
        llSearch = (RelativeLayout) findViewById(R.id.llSearch); // 검색시 포커스를 이동시키기 위한 뷰
        actvSearch = (AppCompatAutoCompleteTextView) findViewById(R.id.actvSearch);
        actvSearch.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                getSupportActionBar().hide();
            }
        });
        ibClose = findViewById(R.id.ibClose);

        // Rx debounce를 통해, 특정 시간(TEXT_INPUT_THRESHOLD)동안 입력이 없는 경우 검색을 요청함
        RxTextView.textChanges(actvSearch)
                .filter(new Func1<CharSequence, Boolean>() {
                    @Override public Boolean call(CharSequence charSequence) { return charSequence.length() > 0; }
                })
                .debounce(TEXT_INPUT_THRESHOLD, TimeUnit.MILLISECONDS)
                .map(new Func1<CharSequence, String>() {
                    @Override public String call(CharSequence charSequence) {
                        return charSequence.toString();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override public void call(String s) {
                        if (!located) return;
                        Log.d("test", "onTextChanged");
                        HashMap<String, Object> bodyMap = new HashMap<>();
                        bodyMap.put("latitude", latitude);
                        bodyMap.put("longitude", longitude);
                        bodyMap.put("keyWord", s.toString());
                        bodyMap.put("restoPhoneNum", restoPhoneNum);
                        requestVendor(bodyMap);

                        rvVendors.scrollToPosition(0);
                    }
                });

        actvSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    getSupportActionBar().hide();
                    ibClose.setVisibility(View.VISIBLE);
                } else {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(actvSearch.getWindowToken(), 0);

                    getSupportActionBar().show();
                    ibClose.setVisibility(View.GONE);
                }
            }
        });

        ibClose.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                eventManager.setSearchEvent(actvSearch.getText().toString());

                llSearch.requestFocus();
                HashMap<String, Object> bodyMap = new HashMap<>();
                Log.d("test", "latitude : " + latitude + "," + "longitude : " + longitude);
                bodyMap.put("latitude", latitude);
                bodyMap.put("longitude", longitude);
                bodyMap.put("restoPhoneNum", restoPhoneNum);
                requestVendor(bodyMap);
                actvSearch.getText().clear();
            }
        });

        retrofit = App.getApplication(getApplicationContext()).getRetrofit();

        apiService = retrofit.create(ApiService.class);

        locationUpdateListener = new LocationManager.LocationUpdateListener() {

            @Override public void onLocationUpdateFailed(Status status) throws IntentSender.SendIntentException {
                status.startResolutionForResult(RecommendActivity.this, REQUEST_CHECK_SETTINGS);
            }

            @Override public void onLocationUpdated(double latitude, double longitude, String locality) {
                Log.d("test:", "recommend activity - latitude : " + latitude + "longitude :" + longitude);
                located = true;

                tvCurrentLocation.setText(locality);

                HashMap<String, Object> bodyMap = new HashMap<>();
                bodyMap.put("latitude", latitude);
                bodyMap.put("longitude", longitude);
                bodyMap.put("restoPhoneNum", restoPhoneNum);

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

    @Override protected void onStart() {
        super.onStart();
        Log.d("test:", "onStart");
        locationManager.connectGoogleApiClient();
    }

    @Override protected void onResume() {
        super.onResume();
        Log.d("test:", "onResume");
        try {
            locationManager.startLocationUpdates();
        } catch (IllegalStateException e) {
            FirebaseCrash.report(e);
        }
    }

    @Override protected void onPause() {
        Log.d("test:", "onPause");
        try {
            locationManager.stopLocationUpdates();
        } catch (IllegalStateException e) {
            FirebaseCrash.report(e);
        }

        super.onPause();
    }

    @Override protected void onStop() {
        Log.d("test:", "onStop");
        try {
            locationManager.disconnectGoogleApiClient();
        } catch (IllegalStateException e) {
            FirebaseCrash.report(e);
        }

        super.onStop();
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {

            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(this, "업체추천 기능을 사용하려면 '위치'를 활성화해야 합니다", Toast.LENGTH_SHORT).show();
                        moveTab(TabActionBar.Tab.VOICE_ORDER);
                        locationManager.disconnectGoogleApiClient();
                        finish();
                        break;
                }
                break;

            case VENDOR_DETAIL:
                int itemPosition = data.getIntExtra("itemPosition", -1);
                if(itemPosition == -1) break;

                rvAdapter.notifyItemChanged(itemPosition);
                break;
        }
    }

    @Override public void onClickNegativeButton() {
        beforeCallDialog.dismiss();
    }

    @Override public void onClickPositiveButton() {
        eventManager.setCallRecommendedVendorEvent();

        beforeCallDialog.dismiss();

        appPreferenceManager.setCallTime(vendorPhoneNumber, System.currentTimeMillis());
        appPreferenceManager.addCalledVendor(vendorPhoneNumber, vendorName);

        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + vendorPhoneNumber));
        startActivity(intent);

        rvAdapter.notifyItemChanged(position);
    }

    private void goToVendorDetail(int itemPosition, String phoneNumber, String vendorName, String vendorAddress,
                                  String majorItems, View sharedView, boolean awsRdb) {

        Intent intent = new Intent(RecommendActivity.this, VendorDetailActivity.class);
        intent.putExtra("itemPosition", itemPosition);
        intent.putExtra("awsRdb", awsRdb);
        intent.putExtra("vendorPhoneNumber", phoneNumber);
        intent.putExtra("vendorName", vendorName);
        intent.putExtra("vendorAddress", vendorAddress);
        intent.putExtra("majorItems", majorItems);

        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this,sharedView,"vendor_detail");
        ActivityCompat.startActivityForResult(this, intent, VENDOR_DETAIL, options.toBundle());
    }

    private void requestVendor(final HashMap<String, Object> bodyMap) {
        apiService
                .getNearbyVendors(bodyMap)
                .enqueue(new Callback<List<Vendor>>() {
                    @Override public void onResponse(Call<List<Vendor>> call, Response<List<Vendor>> response) {
                        if (response.body() != null) {
                            int i = 0;
                            for (Vendor vendor : response.body()) {
                                Log.d("test", i++ + vendor.name);
                            }
                            rvAdapter.setVendors(response.body());
                        } else {
                            rvAdapter.clearVendors();
                        }
                    }

                    @Override public void onFailure(Call<List<Vendor>> call, Throwable t) {
                        Log.d("test", t.toString());
                    }
                });
    }
}
