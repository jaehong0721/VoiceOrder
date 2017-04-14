package com.rena21c.voiceorder.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.FirebaseDatabase;
import com.rena21c.voiceorder.App;
import com.rena21c.voiceorder.R;
import com.rena21c.voiceorder.etc.AppPreferenceManager;
import com.rena21c.voiceorder.etc.PermissionManager;
import com.rena21c.voiceorder.etc.PlayServiceManager;
import com.rena21c.voiceorder.etc.VersionManager;
import com.rena21c.voiceorder.firebase.FirebaseDbManager;
import com.rena21c.voiceorder.firebase.SimpleAuthListener;
import com.rena21c.voiceorder.network.ApiService;
import com.rena21c.voiceorder.network.NetworkUtil;
import com.rena21c.voiceorder.network.NoConnectivityException;
import com.rena21c.voiceorder.pojo.UserToken;
import com.rena21c.voiceorder.util.LauncherUtil;
import com.rena21c.voiceorder.view.dialogs.Dialogs;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class SplashActivity extends BaseActivity {

    private PermissionManager permissionManager;

    private FirebaseDbManager dbManager;
    private Retrofit retrofit;
    private ApiService apiService;
    private AppPreferenceManager appPreferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        dbManager = new FirebaseDbManager(FirebaseDatabase.getInstance());

        permissionManager = PermissionManager.newInstance(this);
        appPreferenceManager = App.getApplication(getApplicationContext()).getPreferenceManager();

        if (appPreferenceManager.getLauncherIconCreated()) {
            LauncherUtil.addLauncherIconToHomeScreen(this, getClass());
            appPreferenceManager.setLauncherIconCreated();
        }

        retrofit = App.getApplication(getApplicationContext()).getRetrofit();

        apiService = retrofit.create(ApiService.class);
    }

    @Override
    protected void onResume() {
        super.onResume();
        permissionManager.requestPermission(new PermissionManager.PermissionsPermittedListener() {
            @Override
            public void onAllPermissionsPermitted() {
                checkPlayService();
            }
        });
    }


    private void checkPlayService() {
        PlayServiceManager.checkPlayServices(SplashActivity.this, new PlayServiceManager.CheckPlayServiceListener() {
            @Override
            public void onNext() {
                checkInternetConnection();
            }
        });
    }

    private void checkInternetConnection() {
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
        VersionManager.checkAppVersion(SplashActivity.this, new VersionManager.MeetRequiredVersionListener() {
            @Override
            public void onMeetRequiredVersion() {
                signInProcess();
            }
        });
    }

    private void signInProcess() {
        appPreferenceManager.initPhoneNumber();
        apiService
                .getToken(appPreferenceManager.getPhoneNumber())
                .enqueue(new Callback<UserToken>() {
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

    private void signIn(String customToken) {
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
                                    restartApp(SplashActivity.this);
                                }
                            }).show();
                        }
                    }
                });
    }

    private void storeFcmToken() {
        dbManager.getFcmToken(appPreferenceManager.getPhoneNumber(), appPreferenceManager.getFcmToken(), new SimpleAuthListener(this) {
            @Override public void onSuccess(Object o) {
                goToMain();
            }
        });
    }


    private void goToMain() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    private void restartApp(Context context) {
        Intent intent = new Intent(context, SplashActivity.class);
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        if (context instanceof Activity) {
            ((Activity) context).finish();
        }
    }
}
