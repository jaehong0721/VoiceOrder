package com.rena21c.voiceorder.activities;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.rena21c.voiceorder.App;
import com.rena21c.voiceorder.R;
import com.rena21c.voiceorder.etc.AppPreferenceManager;
import com.rena21c.voiceorder.etc.IsCheckedComparator;
import com.rena21c.voiceorder.firebase.FirebaseDbManager;
import com.rena21c.voiceorder.firebase.ToastErrorHandlingListener;
import com.rena21c.voiceorder.model.Contact;
import com.rena21c.voiceorder.model.VendorInfo;
import com.rena21c.voiceorder.network.ApiService;
import com.rena21c.voiceorder.pojo.MyPartner;
import com.rena21c.voiceorder.services.SimpleLocationManager;
import com.rena21c.voiceorder.util.ContactsLoader;
import com.rena21c.voiceorder.util.DpToPxConverter;
import com.rena21c.voiceorder.view.DividerItemDecoration;
import com.rena21c.voiceorder.view.actionbar.NavigateBackActionBar;
import com.rena21c.voiceorder.view.adapters.ContactsRecyclerViewAdapter;
import com.rena21c.voiceorder.view.animation.ShowHeightChangeAnimation;
import com.rena21c.voiceorder.viewholder.ContactInfoViewHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class AddPartnerActivity extends BaseActivity implements ContactInfoViewHolder.CheckContactListener,
                                                                ContactsLoader.LoadFinishedListener,
                                                                OnCompleteListener{

    private ContactsLoader contactsLoader;

    private AppPreferenceManager appPreferenceManager;

    private FirebaseDbManager dbManager;

    private SimpleLocationManager simpleLocationManager;

    private HashMap<String,MyPartner> myPartnerMap;
    private HashMap<String,MyPartner> removedMyPartnerMap;

    private ContactsRecyclerViewAdapter contactsAdapter;

    private Button btnRegister;
    private AutoCompleteTextView actvSearch;
    private ImageView ivDelete;

    private boolean isInitialAdd;

    private HashMap<String, Object> uploadPathMap;
    private Retrofit retrofit;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_partner);

        NavigateBackActionBar.createWithActionBar(this, getSupportActionBar())
                .setBackButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                })
                .setTitle("거래처 등록");

        simpleLocationManager = new SimpleLocationManager(this);

        retrofit = App.getApplication(getApplicationContext()).getRetrofit();

        apiService = retrofit.create(ApiService.class);

        myPartnerMap = new HashMap<>();
        removedMyPartnerMap = new HashMap<>();
        uploadPathMap = new HashMap<>();

        contactsLoader = new ContactsLoader(getLoaderManager(), getApplicationContext());
        contactsLoader.setLoadFinishedListener(this);

        appPreferenceManager = App.getApplication(getApplicationContext()).getPreferenceManager();

        dbManager = new FirebaseDbManager(FirebaseDatabase.getInstance());

        RecyclerView rvContacts = (RecyclerView) findViewById(R.id.rvContacts);
        contactsAdapter = new ContactsRecyclerViewAdapter(this);

        rvContacts.setLayoutManager(new LinearLayoutManager(this));
        rvContacts.addItemDecoration(new DividerItemDecoration(getApplicationContext(), R.drawable.shape_divider_recycler_view));
        rvContacts.setAdapter(contactsAdapter);

        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                dbManager.uploadMyPartner(appPreferenceManager.getPhoneNumber(), myPartnerMap, AddPartnerActivity.this);
            }
        });

        actvSearch = (AutoCompleteTextView) findViewById(R.id.actvSearch);
        actvSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                contactsAdapter.getFilter().filter(s);
            }

            @Override public void afterTextChanged(Editable s) {}
        });

        ivDelete = (ImageView) findViewById(R.id.ivDelete);
        ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                actvSearch.setText("");
                actvSearch.clearFocus();
            }
        });

        actvSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override public void onFocusChange(View v, boolean hasFocus) {
                if(v != actvSearch) return;

                if(hasFocus) {
                    ivDelete.setVisibility(View.VISIBLE);
                } else {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(actvSearch.getWindowToken(), 0);

                    ivDelete.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override protected void onStart() {
        super.onStart();

        dbManager.getMyPartnersOnce(appPreferenceManager.getPhoneNumber(), new ToastErrorHandlingListener(this) {

            @Override public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    GenericTypeIndicator<HashMap<String, MyPartner>> partnerMapType = new GenericTypeIndicator<HashMap<String, MyPartner>>() {};
                    myPartnerMap = dataSnapshot.getValue(partnerMapType);
                }
                isInitialAdd = myPartnerMap.size() == 0;
                contactsLoader.startToLoadContacts();
            }
        });
    }

    @Override public void onLoadFinished(ArrayList<Contact> contacts) {
        if(myPartnerMap.size() != 0) {

            for (Contact contact : contacts) {
                if (myPartnerMap.containsKey(contact.phoneNumber)) contact.isChecked = true;
            }

            IsCheckedComparator isCheckedComparator = new IsCheckedComparator();
            Collections.sort(contacts, isCheckedComparator);
        }

        contactsAdapter.setOriginContacts(contacts);
    }

    @Override public void onCheck(Contact contact) {
        contact.isChecked = !contact.isChecked;

        if(contact.isChecked) {
            if(removedMyPartnerMap.containsKey(contact.phoneNumber)) {
                MyPartner restoredMyPartner = removedMyPartnerMap.get(contact.phoneNumber);
                restoredMyPartner.name = contact.name;
                myPartnerMap.put(contact.phoneNumber, restoredMyPartner);
            } else {
                myPartnerMap.put(contact.phoneNumber, new MyPartner(contact.name));
            }
        } else {
            MyPartner removedMyPartner = myPartnerMap.remove(contact.phoneNumber);
            removedMyPartnerMap.put(contact.phoneNumber, removedMyPartner);
        }

        showBtnRegister();
    }

    @Override public void onComplete(@NonNull Task task) {
        if(!task.isSuccessful()) Toast.makeText(this, "거래처 등록에 실패하였습니다", Toast.LENGTH_SHORT).show();

        if(myPartnerMap.size() == 0) {
            finish();
            return;
        }

        //식당에 등록한 내거래처를 db의 전체 vendors 목록에도 저장
        dbManager.getAllVendors(new ToastErrorHandlingListener(this) {
            @Override public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()) {
                    GenericTypeIndicator<HashMap<String, Object>> vendorMapType = new GenericTypeIndicator<HashMap<String, Object>>() {};
                    HashMap<String,Object> vendorMap = dataSnapshot.getValue(vendorMapType);

                    Iterator<String> iterator = myPartnerMap.keySet().iterator();

                    while(iterator.hasNext()) {
                        String phoneNumber = iterator.next();
                        if(vendorMap.containsKey(phoneNumber)) iterator.remove();
                    }
                }

                if(myPartnerMap.size() == 0) {
                    finish();
                    return;
                }

                //서버에게도 목록 전송
                sendAddedPartnersToServer(myPartnerMap);

                for(Map.Entry<String, MyPartner> entry : myPartnerMap.entrySet()) {
                    String phoneNumber = (entry.getKey()).trim();
                    String vendorName = (entry.getValue()).name;

                    if(phoneNumber.length() != 10 && phoneNumber.length() != 11) continue;

                    VendorInfo vendorInfo = new VendorInfo(vendorName, phoneNumber);
                    uploadPathMap.put("/"  + phoneNumber + "/" + "info", vendorInfo);
                }

                dbManager.updateVendors(uploadPathMap, new DatabaseReference.CompletionListener() {
                    @Override public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if(databaseError != null) FirebaseCrash.logcat(Log.WARN, "FIRE_BASE", "내거래처 vendors에 저장 실패 : " + databaseError.getMessage());
                        finish();
                    }
                });
            }
        });
    }

    private void showBtnRegister() {
        if(btnRegister.getHeight() == 0) {
            Animation heightChangeAnimation = new ShowHeightChangeAnimation(btnRegister, DpToPxConverter.convertDpToPx(54, getResources().getDisplayMetrics()));
            heightChangeAnimation.setDuration(200);
            btnRegister.startAnimation(heightChangeAnimation);
        }
        if(myPartnerMap.size() != 0) {
            btnRegister.setText("거래처로 등록" + " (" + myPartnerMap.size() + ")");
        } else {
            btnRegister.setText(isInitialAdd ? "취소" : "모든 거래처 삭제");
        }
    }

    private void sendAddedPartnersToServer(final HashMap<String, MyPartner> myPartnerMap) {

        dbManager.getRestaurantName(appPreferenceManager.getPhoneNumber(), new ToastErrorHandlingListener(this) {
            @Override public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, Object> bodyMap = new HashMap<>();
                double latitude;
                double longitude;
                final String restoName;
                List<HashMap<String, String>> vendors = new ArrayList<>();
                HashMap<String,String> addedPartner = new HashMap<>();

                if(!dataSnapshot.exists() || dataSnapshot.getValue().equals("")) {
                    restoName = appPreferenceManager.getPhoneNumber();
                } else {
                    restoName = (String)dataSnapshot.getValue();
                }
                bodyMap.put("restoName", restoName);

                for(Map.Entry<String, MyPartner> entry : myPartnerMap.entrySet()) {
                    String phoneNumber = (entry.getKey()).trim();
                    String vendorName = (entry.getValue()).name;

                    if(phoneNumber.length() != 10 && phoneNumber.length() != 11) continue;

                    addedPartner.put("phoneNum", phoneNumber);
                    addedPartner.put("name",vendorName);
                    vendors.add(addedPartner);
                }
                bodyMap.put("vendors", vendors);

                Location location = simpleLocationManager.getLocation();
                if(location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();

                    bodyMap.put("latitude", latitude);
                    bodyMap.put("longitude", longitude);
                }

                bodyMap.put("restoPhoneNum", appPreferenceManager.getPhoneNumber());

                callApiToSendAddedPartners(bodyMap);
            }
        });
    }

    private void callApiToSendAddedPartners(HashMap<String,Object> bodyMap) {

        for(Map.Entry<String, Object> entry : bodyMap.entrySet()) {
            Log.d("test", entry.getKey() +"," + entry.getValue());
        }
        apiService
                .sendNotiToRV(bodyMap)
                .enqueue(new Callback<Void>() {
                    @Override public void onResponse(Call<Void> call, Response<Void> response) {
                        if(!response.isSuccessful())
                            FirebaseCrash.log("추가된 내거래처 정보 서버에게 전달실패 : " + response.code());
                    }

                    @Override public void onFailure(Call<Void> call, Throwable t) {
                        FirebaseCrash.log(t.toString());
                    }
                });
    }
}
