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

        TypeToken<Container<HashMap<String,Long>>> callTimeMapTypeToken = new TypeToken<Container<HashMap<String,Long>>>(){};

        Container<HashMap<String,Long>> callTimeMapContainer = getMapContainer("callTimeMapContainer", callTimeMapTypeToken);

        if(callTimeMapContainer == null) {
            callTimeMapContainer = new Container<>();
            callTimeMapContainer.setObject(new HashMap<String, Long>());
        }

        callTimeMapContainer.getObject().put(vendorPhoneNumber, callTime);
        setMapContainer("callTimeMapContainer", callTimeMapContainer);
    }

    public long getCallTime(String vendorPhoneNumber) {

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

    public void setMyPartners(HashMap<String, String> checkedContactMap) {

        TypeToken<Container<HashMap<String,String>>> myPartnerMapTypeToken = new TypeToken<Container<HashMap<String,String>>>(){};

        Container<HashMap<String,String>> myPartnerMapContainer = getMapContainer("myPartnerMapContainer", myPartnerMapTypeToken);

        if(myPartnerMapContainer == null) {
            myPartnerMapContainer = new Container<>();
        }

        myPartnerMapContainer.setObject(checkedContactMap);
        setMapContainer("myPartnerMapContainer", myPartnerMapContainer);
    }

    public HashMap<String,String> getMyPartners() {
        TypeToken<Container<HashMap<String,String>>> myPartnerMapTypeToken = new TypeToken<Container<HashMap<String,String>>>(){};

        Container<HashMap<String,String>> myPartnerMapContainer = getMapContainer("myPartnerMapContainer", myPartnerMapTypeToken);

        return myPartnerMapContainer == null ? new HashMap<String,String>() : myPartnerMapContainer.getObject();
    }

    public void setCalledVendors(HashMap<String, String> calledVendorsMap) {
        TypeToken<Container<HashMap<String,String>>> calledVendorsMapTypeToken = new TypeToken<Container<HashMap<String,String>>>(){};

        Container<HashMap<String,String>> calledVendorsMapContainer = getMapContainer("calledVendorsMapContainer", calledVendorsMapTypeToken);

        if(calledVendorsMapContainer == null) {
            calledVendorsMapContainer = new Container<>();
        }

        calledVendorsMapContainer.setObject(calledVendorsMap);
        setMapContainer("calledVendorsMapContainer", calledVendorsMapContainer);
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
}
