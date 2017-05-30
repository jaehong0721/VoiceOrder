package com.rena21c.voiceorder.etc;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.firebase.crash.FirebaseCrash;

public class AppPreferenceManager {

    private final Context context;
    private final SharedPreferences sharedPreference;

    private String phoneNumber = null;

    public AppPreferenceManager(Context context) {
        this.context = context;
        sharedPreference = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void setUserFirstVisit() {
        sharedPreference
                .edit()
                .putBoolean("isFirst", false)
                .apply();
    }

    public boolean getUserFirstVisit() {
        return sharedPreference
                .getBoolean("isFirst", true);
    }

    public boolean getLauncherIconCreated() {
        return sharedPreference
                .getBoolean("isLauncerIconCreated", true);
    }

    public void setLauncherIconCreated() {
        sharedPreference
                .edit()
                .putBoolean("isLauncerIconCreated", false)
                .apply();
    }

    public void setFcmToken(String token) {
        sharedPreference
                .edit()
                .putString("fcmToken", token)
                .apply();
    }

    public String getFcmToken() {
        return sharedPreference
                .getString("fcmToken", null);
    }

    public void initPhoneNumber() {
        if (getPhoneNumber() != null) return;

        String phoneNumber = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getLine1Number();
        if (phoneNumber == null) {
            phoneNumber = "01000000000";
            FirebaseCrash.logcat(Log.WARN, "PhoneNumber", "전화번호가 없는 기기에서 앱을 실행함");
        }
        if (phoneNumber.substring(0, 3).equals("+82")) {
            phoneNumber = phoneNumber.replace("+82", "0");
        }
        sharedPreference
                .edit()
                .putString("phoneNumber", phoneNumber)
                .apply();
    }

    public String getPhoneNumber() {
        if (phoneNumber == null) {
            phoneNumber = sharedPreference.getString("phoneNumber", null);
        }
        return phoneNumber;
    }

    public void setClickedTab(String clickedTabName) {
        sharedPreference
                .edit()
                .putString("clickedTabName", clickedTabName)
                .apply();
    }

    public String getClickedTab() {
        return sharedPreference
                .getString("clickedTabName", null);
    }
}
