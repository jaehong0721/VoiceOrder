package com.rena21c.voiceorder.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.rena21c.voiceorder.R;
import com.rena21c.voiceorder.network.ApiService;
import com.rena21c.voiceorder.network.RetrofitProvider;
import com.rena21c.voiceorder.widget.SplashDialog;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SplashActivity extends AppCompatActivity {

    String customToken;

    private interface OnSignUpCheckFinishedListener {
        void isMember();
        void isNotMember();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        final SplashDialog splashDialog = SplashDialog.newInstance();

        final String phoneNumber =  ((TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE)).getLine1Number();

        splashDialog.show(getSupportFragmentManager(), "splash");

        userSignUpCheck(phoneNumber, new OnSignUpCheckFinishedListener() {
            @Override
            public void isMember() {
                signIn(customToken, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        splashDialog.dismiss();
                        if (task.isSuccessful()) {
                            goToMain();
                        } else {
                            Toast.makeText(getApplicationContext(), "로그인 실패", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void isNotMember() {
                splashDialog.dismiss();
                goToSignUp();
            }
        });
    }

    private void userSignUpCheck(String phoneNumber, final OnSignUpCheckFinishedListener listener) {

        customToken = null;

        Retrofit retrofit = RetrofitProvider.getInstance();
        ApiService apiService = retrofit.create(ApiService.class);
        Call<UserToken> token = apiService.getToken(phoneNumber);

        token.enqueue(new Callback<UserToken>() {
            @Override
            public void onResponse(Call<UserToken> call, Response<UserToken> response) {

                if(response.code() == 200) {
                    customToken = response.body().getFirebaseCustomAuthToken();
                    listener.isMember();
                }
                else {
                    listener.isNotMember();
                }
            }

            @Override
            public void onFailure(Call<UserToken> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void goToSignUp() {
        startActivity(new Intent(SplashActivity.this, SignUpActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    private void goToMain() {
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    private void signIn(String token, OnCompleteListener<AuthResult> listener) {

        final FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();

        mAuth.signInWithCustomToken(token).addOnCompleteListener(listener);
    }

    public class UserToken {
        private String firebaseCustomAuthToken;

        public void setFirebaseCustomAuthToken(String firebaseCustomAuthToken) {
            this.firebaseCustomAuthToken = firebaseCustomAuthToken;
        }

        public String getFirebaseCustomAuthToken() {
            return firebaseCustomAuthToken;
        }
    }
}
