package com.rena21c.voiceorder.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.rena21c.voiceorder.App;
import com.rena21c.voiceorder.R;
import com.rena21c.voiceorder.etc.AppPreferenceManager;
import com.rena21c.voiceorder.view.actionbar.TabActionBar;
import com.rena21c.voiceorder.view.components.ReplaceableLayout;
import com.rena21c.voiceorder.view.widgets.AddPartnerButton;
import com.rena21c.voiceorder.viewmodel.MyPartnerGuideViewModel;
import com.rena21c.voiceorder.viewmodel.MyPartnerListViewModel;

import java.util.HashMap;


public class MyPartnerActivity extends HasTabActivity implements AddPartnerButton.AddPartnerListener{

    private AppPreferenceManager appPreferenceManager;

    private ReplaceableLayout replaceableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_partner);

        replaceableLayout = (ReplaceableLayout)findViewById(R.id.replaceableLayout);

        appPreferenceManager= App.getApplication(getApplicationContext()).getPreferenceManager();

    }
    
    @Override protected void onResume() {
        super.onResume();
        HashMap<String,String> calledVendors = appPreferenceManager.getCalledVendors();
        HashMap<String,String> myPartners =  appPreferenceManager.getMyPartners();
        HashMap<String, Long> allCallTime = appPreferenceManager.getAllCallTime();

        if(calledVendors.size() != 0 || myPartners.size() != 0) {
            MyPartnerListViewModel myPartnerListViewModel= new MyPartnerListViewModel(this,calledVendors, myPartners, allCallTime);
            View myPartnerListView = myPartnerListViewModel.getView(this);
            replaceableLayout.replaceChildView(myPartnerListView);
        } else {
            MyPartnerGuideViewModel guideViewModel = new MyPartnerGuideViewModel(this);
            View myPartnerGuideView = guideViewModel.getView(LayoutInflater.from(this));
            replaceableLayout.replaceChildView(myPartnerGuideView);
        }
    }

    @Override public void onAddPartner() {
        Intent intent = new Intent(MyPartnerActivity.this, AddPartnerActivity.class);
        startActivity(intent);
    }

    @SuppressWarnings("MissingPermission")
    public void moveToCallApp(String phoneNumber) {
        appPreferenceManager.setCallTime(phoneNumber, System.currentTimeMillis());

        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(intent);
    }

    public void moveToVoiceOrderTab() {
        super.moveTab(TabActionBar.Tab.VOICE_ORDER);
    }
}
