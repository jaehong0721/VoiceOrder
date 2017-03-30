package com.rena21c.voiceorder.activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.rena21c.voiceorder.App;
import com.rena21c.voiceorder.R;
import com.rena21c.voiceorder.etc.PermissionManager;
import com.rena21c.voiceorder.etc.PlayServiceManager;
import com.rena21c.voiceorder.etc.PreferenceManager;
import com.rena21c.voiceorder.etc.VersionManager;
import com.rena21c.voiceorder.model.Order;
import com.rena21c.voiceorder.model.VendorInfo;
import com.rena21c.voiceorder.model.VoiceRecord;
import com.rena21c.voiceorder.network.ApiService;
import com.rena21c.voiceorder.network.NetworkUtil;
import com.rena21c.voiceorder.network.NoConnectivityException;
import com.rena21c.voiceorder.network.RetrofitSingleton;
import com.rena21c.voiceorder.view.dialogs.Dialogs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class SplashActivity extends BaseActivity {

    public class UserToken {
        public String firebaseCustomAuthToken;
    }

    private TextView tvStatus;
    private String phoneNumber;
    private PermissionManager permissionManager;

    private List<String> fileNameList;
    private HashMap<String, HashMap<String, String>> recordedFileMap;
    private HashMap<String, HashMap<String, VoiceRecord>> acceptedOrderMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        tvStatus = (TextView) findViewById(R.id.tvStatus);

        permissionManager = new PermissionManager(
                this,
                new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.RECORD_AUDIO},
                "회원가입을 위한 전화번호, 주문을 위한 녹음 권한을 요청합니다.",
                "앱에서 필요한 권한을 요청을 할 수 없습니다.\n\n" + "서비스를 계속 사용하기 위해서 \"설정\" 버튼을 누르신 후, 권한 탭에서 직접 권한을 허락해 주세요.");

        if (PreferenceManager.getUserFirstVisit(this)) {
            addLauncherIconToHomeScreen();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("lifeCycle", "onActivityResult");
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("lifeCycle", "requestPermission");
        permissionManager.requestPermission(new PermissionManager.PermissionsPermittedListener() {
            @Override
            public void onAllPermissionsPermitted() {
                checkPlayService();
            }
        });

    }

    private void addLauncherIconToHomeScreen() {
        Intent shortcutIntent = new Intent(getApplicationContext(), SplashActivity.class);

        shortcutIntent.setAction(Intent.ACTION_MAIN);

        Intent addIntent = new Intent();
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getResources().getString(R.string.app_name));
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(getApplicationContext(), R.mipmap.ic_launcher));

        addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        addIntent.putExtra("duplicate", false);
        getApplicationContext().sendBroadcast(addIntent);
    }

    private void appendText(String message) {
        tvStatus.append(message + "\n");
    }

    private void checkPlayService() {
        appendText("플레이 서비스 버전을 확인 중입니다.");
        PlayServiceManager.checkPlayServices(SplashActivity.this, new PlayServiceManager.CheckPlayServiceListener() {
            @Override
            public void onNext() {
                checkInternetConnection();
            }
        });
    }

    private void checkInternetConnection() {
        appendText("인터넷 연결을 확인중입니다.");
        if (NetworkUtil.isInternetConnected(getApplicationContext())) {
            checkAppVersion();
        } else {
            Dialogs.showNoInternetConnectivityAlertDialog(this, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
        }
    }

    private void checkAppVersion() {
        appendText("앱의 버전을 확인중입니다.");
        VersionManager.checkAppVersion(SplashActivity.this, new VersionManager.MeetRequiredVersionListener() {
            @Override
            public void onMeetRequiredVersion() {
                signInProcess();
            }
        });
    }

    private void signInProcess() {
        appendText("회원 가입중입니다.");
        phoneNumber = PreferenceManager.setPhoneNumber(getApplicationContext());
        requestToken(new Callback<UserToken>() {
            @Override public void onResponse(Call<UserToken> call, Response<UserToken> response) {
                signIn(response.body().firebaseCustomAuthToken);
            }

            @Override public void onFailure(Call<UserToken> call, Throwable t) {
                if (t instanceof NoConnectivityException) {
                    Toast.makeText(SplashActivity.this, "인터넷이 연결 되어 있지 않습니다. 연결을 확인해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    FirebaseCrash.report(t);
                }
            }
        });
    }

    private void requestToken(final Callback<UserToken> userTokenCallback) {
        appendText("서버에 토큰을 요청합니다.");
        Retrofit retrofit = RetrofitSingleton.getInstance(getApplicationContext());
        ApiService apiService = retrofit.create(ApiService.class);
        Call<UserToken> tokenRequest = apiService.getToken(phoneNumber);
        tokenRequest.enqueue(userTokenCallback);
    }

    private void signIn(String customToken) {
        Log.e("Splash", customToken + "입니다");
        appendText("앱을 시작합니다.");
        FirebaseAuth
                .getInstance()
                .signInWithCustomToken(customToken)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            storeFcmToken();
                        } else {
                            Dialogs.createPlayServiceUpdateWarningDialog(SplashActivity.this, new Dialog.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    reStartApp(SplashActivity.this);
                                }
                            }).show();
                        }
                    }
                });
    }

    private void storeFcmToken() {
        FirebaseDatabase.getInstance().getReference().child("restaurants")
                .child(phoneNumber)
                .child("info")
                .child("fcmId")
                .setValue(PreferenceManager.getFcmToken(getApplicationContext()))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    dataLoad();
                                }
                            }).start();
                        } else {
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void dataLoad() {
        Log.e("lifeCycle", "dataLoad");
        final CountDownLatch latch = new CountDownLatch(2);

        //오퍼레이터 접수 전 데이터 로드
        FirebaseDatabase.getInstance().getReference().child("restaurants")
                .child(phoneNumber)
                .child("recordedOrders")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override public void onDataChange(DataSnapshot dataSnapshot) {
                        GenericTypeIndicator objectMapType = new GenericTypeIndicator<HashMap<String, HashMap<String,String>>>(){};
                        recordedFileMap = (HashMap)dataSnapshot.getValue(objectMapType);
                        fileNameList = getSortedListFromMap(recordedFileMap);
                        latch.countDown();
                    }
                    @Override public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(getApplicationContext(), databaseError.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

        //오퍼레이터 접수 후 데이터 로드
        FirebaseDatabase.getInstance().getReference().child("orders")
                .child("restaurants")
                .orderByKey()
                .startAt(phoneNumber + "_00000000000000")
                .endAt(phoneNumber + "_99999999999999")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override public void onDataChange(DataSnapshot dataSnapshot) {
                        GenericTypeIndicator objectMapType = new GenericTypeIndicator<HashMap<String, HashMap<String, VoiceRecord>>>() {};
                        acceptedOrderMap = (HashMap) dataSnapshot.getValue(objectMapType);
                        latch.countDown();
                    }
                    @Override public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(getApplicationContext(), databaseError.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
        try {
            latch.await();
            dataBinding();
            goToMain();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private List getSortedListFromMap(HashMap<String, HashMap<String, String>> recordedFileMap) {
        List<String> fileNameList = new ArrayList();
        if(recordedFileMap != null) {
            for (HashMap<String, String> fileNameMap : recordedFileMap.values()) {
                fileNameList.add(fileNameMap.get("fileName"));
            }
            Collections.sort(fileNameList, Collections.<String>reverseOrder());
        }
        return fileNameList;
    }

    private void dataBinding() {
        //App의 orders 초기화
        App.getApplication(getApplicationContext()).orders = new ArrayList<>();
        final ArrayList<Order> orders = App.getApplication(getApplicationContext()).orders;

        //데이터 바인딩
        if (acceptedOrderMap == null && !fileNameList.isEmpty()) {
            //접수 된 주문은 없고 접수 전 주문만 있을 때
            for (String fileName : fileNameList) {
                Log.e("case1", fileName);
                String timeStamp = (App.makeTimeFromFileName(fileName));
                orders.add(new Order(timeStamp, null));
            }
        } else if (acceptedOrderMap != null && !fileNameList.isEmpty()) {
            //접수 후 주문과 접수 전 주문이 모두 있을 때
            for (String fileName : fileNameList) {
                Log.e("case3", fileName);
                String timeStamp = (App.makeTimeFromFileName(fileName));
                if (acceptedOrderMap.containsKey(fileName)) {
                    HashMap<String, VoiceRecord> itemHashMap = getVendorName(acceptedOrderMap.get(fileName));
                    orders.add(new Order(timeStamp, itemHashMap));
                } else {
                    orders.add(new Order(timeStamp, null));
                }
            }
        }
    }

    private HashMap getVendorName(final HashMap<String, VoiceRecord> itemHashMap) {
        for (final String vendorPhoneNumber : itemHashMap.keySet()) {
            FirebaseDatabase.getInstance().getReference().child("vendors")
                    .child(vendorPhoneNumber)
                    .child("info")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override public void onDataChange(DataSnapshot dataSnapshot) {
                            VendorInfo vendorInfo = dataSnapshot.getValue(VendorInfo.class);
                            itemHashMap.put(vendorInfo.vendorName, itemHashMap.remove(vendorPhoneNumber));
                        }

                        @Override public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(getApplicationContext(), databaseError.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        return itemHashMap;
    }

    private void goToMain() {
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    public void reStartApp(Context context) {
        Intent intent = new Intent(context, SplashActivity.class);
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        if (context instanceof Activity) {
            ((Activity) context).finish();
        }
    }
}
