package com.rena21c.voiceorder.etc;

import android.content.Context;

import java.util.ArrayList;
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

    public static void storeTimeList(Context context, ArrayList<String> timeList) {

        Set<String> set = new HashSet<String>();
        set.addAll(timeList);

        getDefaultSharedPreferences(context)
                .edit()
                .putStringSet("timeList", set)
                .apply();
    }

    public static ArrayList<String> retriveTimeList(Context context) {

        Set<String> set =  getDefaultSharedPreferences(context).getStringSet("timeList", null);

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
