package com.rena21c.voiceorder.etc;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.firebase.crash.FirebaseCrash;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rena21c.voiceorder.util.Container;
import com.rena21c.voiceorder.view.actionbar.TabActionBar;

import java.util.HashMap;
import java.util.Map;

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

    public void setClickedTab(String clickedTab) {
        sharedPreference
                .edit()
                .putString("clickedTabName", clickedTab)
                .apply();
    }

    public String getClickedTab() {
        return sharedPreference
                .getString("clickedTabName", TabActionBar.Tab.RECOMMEND.toString());
    }

    public void setCallTime(String vendorPhoneNumber, long callTime) {
        Container<Map<String,Long>> callTimeMapContainer = getCallTimeMapContainer();

        if(callTimeMapContainer == null) {
            callTimeMapContainer = createCallTimeMapContainer();
        }

        callTimeMapContainer.getObject().put(vendorPhoneNumber, callTime);
        setCallTimeMapContainer(callTimeMapContainer);
    }

    public long getCallTime(String vendorPhoneNumber) {
        Container<Map<String,Long>> callTimeMapContainer = getCallTimeMapContainer();

        if(callTimeMapContainer == null) return -1;

        Long callTime = callTimeMapContainer.getObject().get(vendorPhoneNumber);

        return callTime == null ? -1 : callTime;
    }

    public Map<String,Long> getAllCallTime() {
        return getCallTimeMapContainer() == null ? null : getCallTimeMapContainer().getObject();
    }

    private Container<Map<String,Long>> getCallTimeMapContainer() {
        Gson gson = new Gson();
        String serializedMap = sharedPreference.getString("callTimeMap", null);
        if(serializedMap != null) {
            return gson.fromJson(serializedMap, new TypeToken<Container<Map<String,Long>>>(){}.getType());
        }
        return null;
    }

    private Container<Map<String,Long>> createCallTimeMapContainer() {
        Map<String, Long> callTimeHistoryMap = new HashMap<>();
        Container<Map<String, Long>> mapContainer = new Container<>();
        mapContainer.setObject(callTimeHistoryMap);

        return mapContainer;
    }

    private void setCallTimeMapContainer(Container<Map<String,Long>> mapContainer) {
        Gson gson = new Gson();
        String serializedMap = gson.toJson(mapContainer);

        sharedPreference
                .edit()
                .putString("callTimeMap", serializedMap)
                .apply();
    }
}
