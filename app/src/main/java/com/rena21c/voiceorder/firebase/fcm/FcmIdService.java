package com.rena21c.voiceorder.firebase.fcm;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.rena21c.voiceorder.etc.AppPreferenceManager;

/**
 * 출처: https://firebase.google.com/docs/cloud-messaging/android/client?hl=ko
 */
public class FcmIdService extends FirebaseInstanceIdService {

    private AppPreferenceManager appPreferenceManager;

    @Override public void onCreate() {
        super.onCreate();
        appPreferenceManager = new AppPreferenceManager(this);
    }

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        storeToken(refreshedToken);
    }

    public void storeToken(String token) {
        appPreferenceManager.setFcmToken(token);
        Log.d("firebase", "Refreshed token: " + token);
    }

}