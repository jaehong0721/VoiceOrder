package com.rena21c.voiceorder.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.rena21c.voiceorder.App;
import com.rena21c.voiceorder.R;
import com.rena21c.voiceorder.etc.PermissionManager;
import com.rena21c.voiceorder.etc.PermissionManager.PermissionsPermittedListener;
import com.rena21c.voiceorder.etc.PreferenceManager;
import com.rena21c.voiceorder.model.Order;
import com.rena21c.voiceorder.model.VendorInfo;
import com.rena21c.voiceorder.model.VoiceRecord;
import com.rena21c.voiceorder.network.ApiService;
import com.rena21c.voiceorder.network.RetrofitSingleton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SplashActivity extends BaseActivity {

    public class UserToken {
        public String firebaseCustomAuthToken;
    }

    interface DataLoadFinishedListener {
        void onFinish();
    }

    private DatabaseReference rootRef;
    private String phoneNumber;
    private PermissionManager permissionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        rootRef = FirebaseDatabase.getInstance().getReference();

        PackageManager pmanager = this.getPackageManager();

        permissionManager = new PermissionManager(
                this,
                new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.RECORD_AUDIO},
                "회원가입을 위한 전화번호, 주문을 위한 녹음 권한을 요청합니다.",
                "앱에서 팔요한 권한을 요청을 할 수 없습니다.\n\n" + "서비스를 계속 사용하기 위해서 \"설정\" 버튼을 누르신 후, 권한 탭에서 직접 권한을 허락해 주세요.",
                new PermissionsPermittedListener() {
                    @Override
                    public void onAllPermissionsPermitted() {
                        Log.d("", "sign in");
                        phoneNumber = PreferenceManager.setPhoneNumber(getApplicationContext());
                        signInProcess();
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        permissionManager.requestPermission();
    }

    private void signInProcess() {
        requestToken(new Callback<UserToken>() {
            @Override
            public void onResponse(Call<UserToken> call, Response<UserToken> response) {
                signIn(response.body().firebaseCustomAuthToken);
            }

            @Override
            public void onFailure(Call<UserToken> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void requestToken(final Callback<UserToken> userTokenCallback) {
        Retrofit retrofit = RetrofitSingleton.getInstance();
        ApiService apiService = retrofit.create(ApiService.class);
        Call<UserToken> tokenRequest = apiService.getToken(phoneNumber);

        tokenRequest.enqueue(userTokenCallback);
    }

    private void signIn(String customToken) {
        FirebaseAuth
                .getInstance()
                .signInWithCustomToken(customToken)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            storeFcmToken();
                            goToMain();
                        } else {
                            Toast.makeText(getApplicationContext(), "로그인 실패", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void storeFcmToken() {
        rootRef.child("restaurants")
                .child(phoneNumber)
                .child("info")
                .child("fcmId")
                .setValue(PreferenceManager.getFcmToken(getApplicationContext()))
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void goToMain() {
        dataLoad(new DataLoadFinishedListener() {
            @Override
            public void onFinish() {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        });
    }

    private void dataLoad(final DataLoadFinishedListener dataLoadFinishedListener) {

        App.getApplication(getApplicationContext()).orders = new ArrayList<>();
        final ArrayList<Order> orders = App.getApplication(getApplicationContext()).orders;

        rootRef.child("orders")
                .child("restaurants")
                .orderByKey()
                .startAt(phoneNumber + "_00000000000000")
                .endAt(phoneNumber + "_99999999999999")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        GenericTypeIndicator objectMapType = new GenericTypeIndicator<HashMap<String, HashMap<String, VoiceRecord>>>() {};
                        HashMap<String, HashMap<String, VoiceRecord>> objectMap = (HashMap) dataSnapshot.getValue(objectMapType);

                        List<String> fileNameList = new ArrayList(PreferenceManager.getFileNameList(getApplicationContext()));
                        Collections.sort(fileNameList, Collections.<String>reverseOrder());

                        if (objectMap == null && fileNameList.isEmpty()) {
                            //DB와 APP 모두 데이터가 없으면 데이터 바인딩 없이 빠져나감
                            dataLoadFinishedListener.onFinish();
                            return;
                        } else if (objectMap == null && !fileNameList.isEmpty()) {
                            //DB에는 데이터가 없고 APP에만 데이터가 있으면 APP데이터만 바인딩해줌
                            for (String fileName : fileNameList) {
                                Log.e("fileName", fileName);
                                String timeStamp = (App.makeTimeFromFileName(fileName));
                                orders.add(new Order(timeStamp, null));
                            }
                        } else if (objectMap != null && fileNameList.isEmpty()) {
                            //DB에는 데이터가 있고 APP에는 없으면 DB데이터만 바인딩해줌
                            for (String fileName : objectMap.keySet()) {
                                String timeStamp = (App.makeTimeFromFileName(fileName));
                                HashMap<String, VoiceRecord> itemHashMap = getVendorName(objectMap.get(fileName));
                                orders.add(new Order(timeStamp, itemHashMap));
                            }
                        } else if (objectMap != null && !fileNameList.isEmpty()) {
                            //DB와 APP모두 데이터가 있으면 비교해서 데이터를 바인딩해줌
                            for (String fileName : fileNameList) {
                                Log.e("fileName", fileName);
                                String timeStamp = (App.makeTimeFromFileName(fileName));
                                if (objectMap.containsKey(fileName)) {
                                    HashMap<String, VoiceRecord> itemHashMap = getVendorName(objectMap.get(fileName));
                                    orders.add(new Order(timeStamp, itemHashMap));
                                } else {
                                    orders.add(new Order(timeStamp, null));
                                }
                            }
                        }
                        dataLoadFinishedListener.onFinish();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(getApplicationContext(), databaseError.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private HashMap getVendorName(final HashMap<String, VoiceRecord> itemHashMap) {

        for (final String vendorPhoneNumber : itemHashMap.keySet()) {
            rootRef.child("vendors")
                    .child(vendorPhoneNumber)
                    .child("info")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            VendorInfo vendorInfo = dataSnapshot.getValue(VendorInfo.class);
                            itemHashMap.put(vendorInfo.vendorName, itemHashMap.remove(vendorPhoneNumber));
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(getApplicationContext(), databaseError.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        return itemHashMap;
    }
}
