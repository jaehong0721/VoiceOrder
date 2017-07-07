package com.rena21c.voiceorder.firebase;


import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

public class AnalyticsEventManager {

    private static final String USER_ID = "user_id";
    private static final String KEYWORD = "keyword";
    private static final String SEARCH_EVENT = "search_event";
    private static final String CALL_RECOMMENDED_VENDOR = "call_recommended_vendor";
    private static final String CALL_MY_PARTNER = "call_my_partner";
    private static final String VOICE_ORDER = "voice_order";
    private static final String REPLAY_ORDER = "replay_order";


    private final FirebaseAnalytics instance;
    private final String userPhoneNumber;

    public AnalyticsEventManager(FirebaseAnalytics instance, String userPhoneNumber) {
        this.instance = instance;
        this.userPhoneNumber = userPhoneNumber;
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

        instance.logEvent(CALL_RECOMMENDED_VENDOR, bundle);
    }

    public void setCallMyPartnerEvent() {
        Bundle bundle = new Bundle();
        bundle.putString(USER_ID, userPhoneNumber);

        instance.logEvent(CALL_MY_PARTNER, bundle);
    }

    public void setVoiceOrderEvent() {
        Bundle bundle = new Bundle();
        bundle.putString(USER_ID, userPhoneNumber);

        instance.logEvent(VOICE_ORDER, bundle);
    }

    public void setRePlayOrderEvent() {
        Bundle bundle = new Bundle();
        bundle.putString(USER_ID, userPhoneNumber);

        instance.logEvent(REPLAY_ORDER, bundle);
    }
}
