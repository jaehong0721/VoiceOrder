package com.rena21c.voiceorder.fcm;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.rena21c.voiceorder.etc.PreferenceManager;

/**
 * 출처: https://firebase.google.com/docs/cloud-messaging/android/client?hl=ko
 */
public class FcmIdService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        storeToken(refreshedToken);
    }

    public void storeToken(String token) {
        Log.d("firebase", "Refreshed token: " + token);
        PreferenceManager.setFcmToken(getApplicationContext(), token);
    }

}