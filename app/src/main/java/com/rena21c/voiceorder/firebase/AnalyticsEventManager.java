package com.rena21c.voiceorder.firebase;


import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

public class AnalyticsEventManager {

    private static final String USER_ID = "user_id";
    private static final String KEYWORD = "keyword";

    private static final String SEARCH_EVENT = "search_event";
    private static final String CALL_RECOMMENDED_VENDOR_EVENT = "call_recommended_vendor_event";
    private static final String CALL_MY_PARTNER_EVENT = "call_my_partner_event";
    private static final String VOICE_ORDER_EVENT = "voice_order_event";
    private static final String REPLAY_ORDER_EVENT = "replay_order_event";
    private static final String ADD_MY_PARTNER_EVENT = "add_my_partner_event";
    private static final String CLICK_RECOMMEND_TAB = "click_recommend_tab";
    private static final String CLICK_VOICE_ORDER_TAB = "click_voice_order_tab";
    private static final String CLICK_MY_PARTNER_TAB = "click_my_partner_tab";


    private final FirebaseAnalytics instance;
    private final String userPhoneNumber;

    public AnalyticsEventManager(FirebaseAnalytics instance, String userPhoneNumber) {
        this.instance = instance;
        this.userPhoneNumber = userPhoneNumber;
        instance.setUserId(userPhoneNumber);
    }

    public void setSearchEvent(String keyword) {
        Bundle bundle = new Bundle();
        bundle.putString(USER_ID, userPhoneNumber);
        bundle.putString(KEYWORD, keyword);
        instance.logEvent(SEARCH_EVENT, bundle);
    }

    public void setCallRecommendedVendorEvent() {
        Bundle bundle = new Bundle();
        bundle.putString(USER_ID, userPhoneNumber);

        instance.logEvent(CALL_RECOMMENDED_VENDOR_EVENT, bundle);
    }

    public void setCallMyPartnerEvent() {
        Bundle bundle = new Bundle();
        bundle.putString(USER_ID, userPhoneNumber);

        instance.logEvent(CALL_MY_PARTNER_EVENT, bundle);
    }

    public void setVoiceOrderEvent() {
        Bundle bundle = new Bundle();
        bundle.putString(USER_ID, userPhoneNumber);

        instance.logEvent(VOICE_ORDER_EVENT, bundle);
    }

    public void setRePlayOrderEvent() {
        Bundle bundle = new Bundle();
        bundle.putString(USER_ID, userPhoneNumber);

        instance.logEvent(REPLAY_ORDER_EVENT, bundle);
    }

    public void setAddMyPartnerEvent() {
        Bundle bundle = new Bundle();
        bundle.putString(USER_ID, userPhoneNumber);

        instance.logEvent(ADD_MY_PARTNER_EVENT, bundle);
    }

    public void setRecommendTabClickEvent() {
        Bundle bundle = new Bundle();
        bundle.putString(USER_ID, userPhoneNumber);

        instance.logEvent(CLICK_RECOMMEND_TAB, bundle);
    }

    public void setVoiceOrderTabClickEvent() {
        Bundle bundle = new Bundle();
        bundle.putString(USER_ID, userPhoneNumber);

        instance.logEvent(CLICK_VOICE_ORDER_TAB, bundle);
    }

    public void setMyPartnerTabClickEvent() {
        Bundle bundle = new Bundle();
        bundle.putString(USER_ID, userPhoneNumber);

        instance.logEvent(CLICK_MY_PARTNER_TAB, bundle);
    }
}
