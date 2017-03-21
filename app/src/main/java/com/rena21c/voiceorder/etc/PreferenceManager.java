package com.rena21c.voiceorder.etc;

import android.content.Context;
import android.telephony.TelephonyManager;

import java.util.HashSet;
import java.util.Set;

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
        String phoneNumber = ((TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE)).getLine1Number();
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

    public static void setFileName(Context context, String fileName) {
        HashSet<String> fileNameList = (HashSet)getFileNameList(context);
        fileNameList.add(fileName);
        getDefaultSharedPreferences(context)
                .edit()
                .remove("fileNameList")
                .apply();

        getDefaultSharedPreferences(context)
                .edit()
                .putStringSet("fileNameList", fileNameList)
                .apply();
    }

    public static Set<String> getFileNameList(Context context) {
        return getDefaultSharedPreferences(context)
                .getStringSet("fileNameList", new HashSet<String>());
    }
}
