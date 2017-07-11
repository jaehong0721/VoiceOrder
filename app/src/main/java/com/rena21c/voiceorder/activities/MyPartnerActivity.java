package com.rena21c.voiceorder.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.firebase.database.FirebaseDatabase;
import com.rena21c.voiceorder.App;
import com.rena21c.voiceorder.R;
import com.rena21c.voiceorder.etc.AppPreferenceManager;
import com.rena21c.voiceorder.firebase.AnalyticsEventManager;
import com.rena21c.voiceorder.firebase.FirebaseDbManager;
import com.rena21c.voiceorder.view.actionbar.TabActionBar;
import com.rena21c.voiceorder.view.widgets.AddPartnerButton;
import com.rena21c.voiceorder.viewmodel.MyPartnerGuideViewModel;
import com.rena21c.voiceorder.viewmodel.MyPartnerListViewModel;


public class MyPartnerActivity extends HasTabActivity implements AddPartnerButton.AddPartnerListener,
                                                                 MyPartnerListViewModel.DataSetSizeChangedListener {
    private AnalyticsEventManager eventManager;

    private View myPartnerListView;
    private View myPartnerGuideView;

    private View showingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_partner);

        RelativeLayout rootView = (RelativeLayout) findViewById(R.id.rootView);

        AppPreferenceManager appPreferenceManager = App.getApplication(getApplicationContext()).getPreferenceManager();
        eventManager = App.getApplication(getApplicationContext()).getEventManager();

        FirebaseDbManager dbManager = new FirebaseDbManager(FirebaseDatabase.getInstance());

        MyPartnerListViewModel myPartnerListViewModel = new MyPartnerListViewModel (this,
                                                                                    this,
                appPreferenceManager,
                dbManager);
        myPartnerListView = myPartnerListViewModel.getView(this);
        myPartnerListView.setVisibility(View.GONE);

        MyPartnerGuideViewModel guideViewModel = new MyPartnerGuideViewModel(this);
        myPartnerGuideView = guideViewModel.getView(LayoutInflater.from(this));
        myPartnerGuideView.setVisibility(View.GONE);

        rootView.addView(myPartnerListView);
        rootView.addView(myPartnerGuideView);
    }

    @Override public void onAddPartner() {
        eventManager.setAddMyPartnerEvent();

        Intent intent = new Intent(MyPartnerActivity.this, AddPartnerActivity.class);
        startActivity(intent);
    }

    @Override public void onDataSetSizeChanged(int size) {
        if(size == 0) {
            showGuide();
        } else {
            showMyPartnerList();
        }
    }

    @SuppressWarnings("MissingPermission")
    public void moveToCallApp(String phoneNumber) {
        eventManager.setCallMyPartnerEvent();

        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(intent);
    }

    public void moveToVoiceOrderTab() {
        super.moveTab(TabActionBar.Tab.VOICE_ORDER);
    }

    private void showMyPartnerList() {
        if(showingView == myPartnerListView) return;

        showingView = myPartnerListView;

        myPartnerGuideView.setVisibility(View.GONE);
        myPartnerListView.setVisibility(View.VISIBLE);
    }

    private void showGuide() {
        if(showingView == myPartnerGuideView) return;

        showingView = myPartnerGuideView;

        myPartnerListView.setVisibility(View.GONE);
        myPartnerGuideView.setVisibility(View.VISIBLE);
    }
}
