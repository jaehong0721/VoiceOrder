package com.rena21c.voiceorder.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.rena21c.voiceorder.R;
import com.rena21c.voiceorder.etc.PermissionManager;
import com.rena21c.voiceorder.etc.PermissionManager.PermissionsPermittedListener;
import com.rena21c.voiceorder.network.ApiService;
import com.rena21c.voiceorder.network.RetrofitSingleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SplashActivity extends BaseActivity {

    FirebaseUser firebaseUser;
    private PermissionManager permissionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        PackageManager pmanager = this.getPackageManager();
        boolean mic = pmanager.hasSystemFeature(PackageManager.FEATURE_MICROPHONE);

        permissionManager = new PermissionManager(
                this,
                new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.RECORD_AUDIO},
                "회원가입을 위한 전화번호, 주문을 위한 녹음 권한을 요청합니다.",
                "앱에서 팔요한 권한을 요청을 할 수 없습니다.\n\n" + "서비스를 계속 사용하기 위해서 \"설정\" 버튼을 누르신 후, 권한 탭에서 직접 권한을 허락해 주세요.",
                new PermissionsPermittedListener() {
                    @Override public void onAllPermissionsPermitted() {
                        Log.d("", "sign in");
                        signInProcess();
                    }
                });
    }

    @Override protected void onResume() {
        super.onResume();
        permissionManager.requestPermission();
    }

    private void signInProcess() {
        if (isSignedIn()) {
            goToMain();
        } else {
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
    }

    private boolean isSignedIn() {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        return firebaseUser != null;
    }

    private void requestToken(final Callback<UserToken> userTokenCallback) {
        String phoneNumber = ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).getLine1Number();
        if (phoneNumber.substring(0, 3).equals("+82")) {
            phoneNumber = phoneNumber.replace("+82", "0");
        }
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
                            Log.e("SplashActivity", "새로 가입함");
                            goToMain();
                        } else {
                            Toast.makeText(getApplicationContext(), "로그인 실패", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void goToMain() {
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    public class UserToken {

        public String firebaseCustomAuthToken;

    }

}
