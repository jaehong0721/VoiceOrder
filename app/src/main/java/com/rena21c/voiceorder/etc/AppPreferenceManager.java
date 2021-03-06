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
import com.rena21c.voiceorder.util.StringUtil;
import com.rena21c.voiceorder.view.actionbar.TabActionBar;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

public class AppPreferenceManager extends Observable {

    private final Context context;
    private final SharedPreferences sharedPreference;

    private String phoneNumber = null;

    public AppPreferenceManager(Context context) {
        this.context = context;
        sharedPreference = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void setUserFirstVisit() {
        if(!getUserFirstVisit()) return;

        sharedPreference
                .edit()
                .putBoolean("isFirstVisit", false)
                .apply();
    }

    public boolean getUserFirstVisit() {
        return sharedPreference
                .getBoolean("isFirstVisit", true);
    }

    public void setUserFirstRecord() {
        sharedPreference
                .edit()
                .putBoolean("isFirst", false)
                .apply();
    }

    public boolean getUserFirstRecord() {
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

        if (phoneNumber.startsWith("+82")) {
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
        vendorPhoneNumber = StringUtil.removeSpecialLetter(vendorPhoneNumber);

        TypeToken<Container<HashMap<String,Long>>> callTimeMapTypeToken = new TypeToken<Container<HashMap<String,Long>>>(){};

        Container<HashMap<String,Long>> callTimeMapContainer = getMapContainer("callTimeMapContainer", callTimeMapTypeToken);

        if(callTimeMapContainer == null) {
            callTimeMapContainer = new Container<>();
            callTimeMapContainer.setObject(new HashMap<String, Long>());
        }

        callTimeMapContainer.getObject().put(vendorPhoneNumber, callTime);
        setMapContainer("callTimeMapContainer", callTimeMapContainer);
        setChanged();
        notifyObservers("callTimes");
    }

    public long getCallTime(String vendorPhoneNumber) {
        vendorPhoneNumber = StringUtil.removeSpecialLetter(vendorPhoneNumber);

        TypeToken<Container<HashMap<String,Long>>> callTimeMapTypeToken = new TypeToken<Container<HashMap<String,Long>>>(){};

        Container<HashMap<String,Long>> callTimeMapContainer = getMapContainer("callTimeMapContainer", callTimeMapTypeToken);

        if(callTimeMapContainer == null) return -1;

        Long callTime = callTimeMapContainer.getObject().get(vendorPhoneNumber);

        return callTime == null ? -1 : callTime;
    }

    public HashMap<String,Long> getAllCallTime() {

        TypeToken<Container<HashMap<String,Long>>> callTimeMapTypeToken = new TypeToken<Container<HashMap<String,Long>>>(){};

        Container<HashMap<String,Long>> callTimeMapContainer = getMapContainer("callTimeMapContainer", callTimeMapTypeToken);
        return callTimeMapContainer == null ? new HashMap<String,Long>() : callTimeMapContainer.getObject();
    }
    
    public void addCalledVendor(String phoneNumber, String name) {
        HashMap<String, String> calledVendorsMap = getCalledVendors();

        if(calledVendorsMap == null)
            calledVendorsMap = new HashMap<>();

        calledVendorsMap.put(StringUtil.removeSpecialLetter(phoneNumber), name);

        TypeToken<Container<HashMap<String,String>>> calledVendorsMapTypeToken = new TypeToken<Container<HashMap<String,String>>>(){};

        Container<HashMap<String,String>> calledVendorsMapContainer = getMapContainer("calledVendorsMapContainer", calledVendorsMapTypeToken);

        if(calledVendorsMapContainer == null) {
            calledVendorsMapContainer = new Container<>();
        }

        calledVendorsMapContainer.setObject(calledVendorsMap);
        setMapContainer("calledVendorsMapContainer", calledVendorsMapContainer);
        setChanged();
        notifyObservers("calledVendors");
    }

    public HashMap<String,String> getCalledVendors() {
        TypeToken<Container<HashMap<String,String>>> calledVendorsMapTypeToken = new TypeToken<Container<HashMap<String,String>>>(){};

        Container<HashMap<String,String>> calledVendorsMapContainer = getMapContainer("calledVendorsMapContainer", calledVendorsMapTypeToken);

        return calledVendorsMapContainer == null ? new HashMap<String,String>() : calledVendorsMapContainer.getObject();
    }

    private <T extends Map> Container<T> getMapContainer(String key, TypeToken<Container<T>> typeToken) {
        Gson gson = new Gson();
        String serializedMap = sharedPreference.getString(key, null);
        if(serializedMap != null) {
            return gson.fromJson(serializedMap, typeToken.getType());
        }
        return null;
    }

    private <T extends Map> void setMapContainer(String key, Container<T> mapContainer) {
        Gson gson = new Gson();
        String serializedMap = gson.toJson(mapContainer);

        sharedPreference
                .edit()
                .putString(key, serializedMap)
                .apply();
    }

    public void setEstimateKey(String estimateKey) {
        sharedPreference
                .edit()
                .putString("estimateKey", estimateKey)
                .apply();
    }

    public String getEstimateKey() {
        return sharedPreference
                .getString("estimateKey", null);
    }
}
