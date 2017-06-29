package com.rena21c.voiceorder.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.rena21c.voiceorder.App;
import com.rena21c.voiceorder.R;
import com.rena21c.voiceorder.etc.AppPreferenceManager;
import com.rena21c.voiceorder.firebase.FirebaseDbManager;
import com.rena21c.voiceorder.firebase.ToastErrorHandlingListener;
import com.rena21c.voiceorder.view.actionbar.TabActionBar;
import com.rena21c.voiceorder.view.components.ReplaceableLayout;
import com.rena21c.voiceorder.view.widgets.AddPartnerButton;
import com.rena21c.voiceorder.viewmodel.MyPartnerGuideViewModel;
import com.rena21c.voiceorder.viewmodel.MyPartnerListViewModel;


public class MyPartnerActivity extends HasTabActivity implements AddPartnerButton.AddPartnerListener,
                                                                 MyPartnerListViewModel.DataSetSizeChangedListener {

    private AppPreferenceManager appPreferenceManager;
    private FirebaseDbManager dbManager;

    private ReplaceableLayout replaceableLayout;

    private View myPartnerListView;
    private View myPartnerGuideView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_partner);

        replaceableLayout = (ReplaceableLayout)findViewById(R.id.replaceableLayout);

        appPreferenceManager= App.getApplication(getApplicationContext()).getPreferenceManager();

        dbManager = new FirebaseDbManager(FirebaseDatabase.getInstance());

        MyPartnerListViewModel myPartnerListViewModel = new MyPartnerListViewModel (this,
                                                                                    this,
                                                                                    appPreferenceManager,
                                                                                    dbManager);
        myPartnerListView = myPartnerListViewModel.getView(this);

        MyPartnerGuideViewModel guideViewModel = new MyPartnerGuideViewModel(this);
        myPartnerGuideView = guideViewModel.getView(LayoutInflater.from(this));

        final boolean hasCalledVendors = appPreferenceManager.getCalledVendors().size() != 0;

        dbManager.hasMyPartner(appPreferenceManager.getPhoneNumber(), new ToastErrorHandlingListener(this) {
            @Override public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("vendors") || hasCalledVendors) {
                    showMyPartnerList();
                } else {
                    showGuide();
                }
            }
        });
    }

    @Override public void onAddPartner() {
        Intent intent = new Intent(MyPartnerActivity.this, AddPartnerActivity.class);
        startActivity(intent);
    }

    @Override public void onDataSetSizeChanged(int size) {
        if(size == 0) showGuide();
    }

    @SuppressWarnings("MissingPermission")
    public void moveToCallApp(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(intent);
    }

    public void moveToVoiceOrderTab() {
        super.moveTab(TabActionBar.Tab.VOICE_ORDER);
    }

    private void showMyPartnerList() {
        replaceableLayout.replaceChildView(myPartnerListView);
    }

    private void showGuide() {
        replaceableLayout.replaceChildView(myPartnerGuideView);
    }
}
