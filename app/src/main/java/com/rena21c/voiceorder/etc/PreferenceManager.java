package com.rena21c.voiceorder.etc;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.firebase.crash.FirebaseCrash;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class PreferenceManager {

    public static void setUserFirstVisit(Context context) {
        getDefaultSharedPreferences(context)
                .edit()
                .putBoolean("isFirst", false)
                .apply();
    }

    public static boolean getUserFirstVisit(Context context) {
        return getDefaultSharedPreferences(context)
                .getBoolean("isFirst", true);
    }

    public static boolean getLauncherIconCreated(Context context) {
        return getDefaultSharedPreferences(context)
                .getBoolean("isLauncerIconCreated", true);
    }

    public static void setLauncherIconCreated(Context context) {
        getDefaultSharedPreferences(context)
                .edit()
                .putBoolean("isLauncerIconCreated", false)
                .apply();
    }

    public static void setFcmToken(Context context, String token) {
        getDefaultSharedPreferences(context)
                .edit()
                .putString("fcmToken", token)
                .apply();
    }

    public static String getFcmToken(Context context) {
        return getDefaultSharedPreferences(context)
                .getString("fcmToken", null);
    }

    public static String setPhoneNumber(Context context) {
        String phoneNumber = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getLine1Number();
        if (phoneNumber == null) {
            phoneNumber = "01000000000";
            FirebaseCrash.logcat(Log.WARN, "PhoneNumber", "전화번호가 없는 기기에서 앱을 실행함");
        }
        if (phoneNumber.substring(0, 3).equals("+82")) {
            phoneNumber = phoneNumber.replace("+82", "0");
        }
        getDefaultSharedPreferences(context)
                .edit()
                .putString("phoneNumber", phoneNumber)
                .apply();
        return phoneNumber;
    }

    public static String getPhoneNumber(Context context) {
        return getDefaultSharedPreferences(context)
                .getString("phoneNumber", null);
    }
}
