package com.rena21c.voiceorder.activities;

import android.os.Bundle;
import android.view.View;

import com.rena21c.voiceorder.App;
import com.rena21c.voiceorder.R;
import com.rena21c.voiceorder.etc.AppPreferenceManager;
import com.rena21c.voiceorder.view.components.ReplaceableLayout;


public class MyPartnerActivity extends HasTabActivity {

    private AppPreferenceManager appPreferenceManager;

    private ReplaceableLayout replaceableLayout;
    private View myPartnerGuide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_partner);

        replaceableLayout = (ReplaceableLayout)findViewById(R.id.replaceableLayout);
        myPartnerGuide = getLayoutInflater().inflate(R.layout.layout_component_my_partner_guide, replaceableLayout, false);

        appPreferenceManager= App.getApplication(getApplicationContext()).getPreferenceManager();
    }

    @Override protected void onResume() {
        super.onResume();
        replaceableLayout.removeAllViews();
        if(appPreferenceManager.getAllCallTime() == null) replaceableLayout.replaceChildView(myPartnerGuide);
    }
}
