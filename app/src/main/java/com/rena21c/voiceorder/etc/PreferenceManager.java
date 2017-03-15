package com.rena21c.voiceorder.etc;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class PreferenceManager {

    public static void storeTimeList(Context context, ArrayList<String> timeList) {
        SharedPreferences sharedPreferences = getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> set = new HashSet<String>();
        set.addAll(timeList);
        editor.putStringSet("timeList", set);
        editor.apply();
    }

    public static ArrayList<String> retriveTimeList(Context context) {
        SharedPreferences sharedPreferences = getDefaultSharedPreferences(context);
        Set<String> set = sharedPreferences.getStringSet("timeList", null);
        if(set == null) {
            return null;
        }
        else {
            ArrayList<String> timeList = new ArrayList<>();
            timeList.addAll(set);
            return timeList;
        }
    }
}
