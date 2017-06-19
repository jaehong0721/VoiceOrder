package com.rena21c.voiceorder.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.rena21c.voiceorder.App;
import com.rena21c.voiceorder.R;
import com.rena21c.voiceorder.etc.AppPreferenceManager;
import com.rena21c.voiceorder.view.components.ReplaceableLayout;
import com.rena21c.voiceorder.view.widgets.AddPartnerButton;
import com.rena21c.voiceorder.viewmodel.MyPartnerGuideViewModel;


public class MyPartnerActivity extends HasTabActivity implements AddPartnerButton.AddPartnerListener{

    private AppPreferenceManager appPreferenceManager;

    private MyPartnerGuideViewModel guideViewModel;

    private ReplaceableLayout replaceableLayout;
    private View myPartnerGuideView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_partner);

        replaceableLayout = (ReplaceableLayout)findViewById(R.id.replaceableLayout);

        appPreferenceManager= App.getApplication(getApplicationContext()).getPreferenceManager();

        guideViewModel = new MyPartnerGuideViewModel(this);
        myPartnerGuideView = guideViewModel.getView(LayoutInflater.from(this));
    }
    
    @Override protected void onResume() {
        super.onResume();
        if(appPreferenceManager.getAllCallTime().size() == 0) replaceableLayout.replaceChildView(myPartnerGuideView);
    }

    @Override public void onAddPartner() {
        Intent intent = new Intent(MyPartnerActivity.this, AddPartnerActivity.class);
        startActivity(intent);
    }
}
