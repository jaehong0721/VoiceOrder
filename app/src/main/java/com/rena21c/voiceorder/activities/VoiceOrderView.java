package com.rena21c.voiceorder.activities;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.GenericTypeIndicator;
import com.rena21c.voiceorder.R;
import com.rena21c.voiceorder.firebase.FirebaseDbManager;
import com.rena21c.voiceorder.model.VoiceRecord;
import com.rena21c.voiceorder.util.FileNameUtil;
import com.rena21c.voiceorder.view.adapters.OrderViewPagerAdapter;
import com.rena21c.voiceorder.view.adapters.SimpleViewPagerSelectedListener;
import com.rena21c.voiceorder.view.components.ReplaceableLayout;
import com.rena21c.voiceorder.view.dialogs.Dialogs;
import com.rena21c.voiceorder.view.widgets.RecordAndStopButton;
import com.rena21c.voiceorder.view.widgets.ViewPagerIndicator;

import java.util.HashMap;

public class VoiceOrderView implements RecordAndStopButton.activateRecorderListener {

    public static final int NO_INTERNAL_MEMORY = 0;
    public static final int NO_INTERNET_CONNECT = 1;

    private VoiceOrderActivity activity;

    private ReplaceableLayout replaceableLayout;
    private RecordAndStopButton recordAndStopButton;
    private View recordingLayout;

    private OrderViewPagerAdapter orderViewPagerAdapter;
    private ViewPager orderViewPager;
    private ViewPagerIndicator viewPagerIndicator;
    private View viewPager;

    public VoiceOrderView(VoiceOrderActivity activity) {
        this.activity = activity;

        replaceableLayout = (ReplaceableLayout) activity.findViewById(R.id.replaceableLayout);
        recordingLayout = activity.getLayoutInflater().inflate(R.layout.layout_component_recording, replaceableLayout, false);

        recordAndStopButton = (RecordAndStopButton) activity.findViewById(R.id.btnRecordAndStop);
        recordAndStopButton.setListener(this);
    }

    public void initView(FirebaseDbManager dbManager) {

        LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        viewPager = layoutInflater.inflate(R.layout.layout_component_order_view_pager, replaceableLayout, false);

        viewPagerIndicator = (ViewPagerIndicator) viewPager.findViewById(R.id.viewPagerIndicator);
        orderViewPager = (ViewPager) viewPager.findViewById(R.id.viewPager);

        orderViewPagerAdapter = new OrderViewPagerAdapter(activity, dbManager, new OrderViewPagerAdapter.ItemCountChangedListener() {
            @Override public void itemCountChange(int count) {
                viewPagerIndicator.changeDot(count);
                if (count == 1) viewPagerIndicator.selectDot(0);
            }
        });

        orderViewPager.setAdapter(orderViewPagerAdapter);
        orderViewPager.addOnPageChangeListener(new SimpleViewPagerSelectedListener() {
            @Override public void onPageSelected(int position) {
                viewPagerIndicator.selectDot(position);
            }
        });
    }

    public void setView(boolean shouldShowGuide) {
        if (shouldShowGuide) {
            setGuide();
        } else {
            setNormal();
        }
    }

    public void addTimeStamp(String fileName) {
        String timeStamp = FileNameUtil.getTimeFromFileName(fileName);
        orderViewPagerAdapter.addTimeStamp(timeStamp);
    }

    private void setGuide() {
        recordAndStopButton.setInitHeight(recordAndStopButton.HEIGHT_WITH_GUIDE_LAYOUT);
        View recordGuideLayout = activity.getLayoutInflater().inflate(R.layout.layout_component_record_guide, replaceableLayout, false);
        replaceableLayout.replaceChildView(recordGuideLayout);
    }

    private void setNormal() {
        recordAndStopButton.setInitHeight(recordAndStopButton.HEIGHT_WITH_ORDER_LIST_LAYOUT);
        replaceableLayout.replaceChildView(viewPager);
    }

    public void replaceViewToRecording() {
        replaceableLayout.replaceChildView(recordingLayout);
        recordAndStopButton.setStopViewState();
    }

    public void replaceViewToUnRecording() {
        replaceableLayout.replaceChildView(viewPager);
        recordAndStopButton.setRecordViewState();
    }

    public void showDialog(int what) {
        switch (what) {
            case 0:
                Dialogs.showNoAvailableInternalMemoryDialog(activity, null);
                break;

            case 1:
                Dialogs.showNoInternetConnectivityAlertDialog(activity, null);
                break;
        }
    }

    public void addEmptyOrderToViewPager(String timeStamp) {
        orderViewPagerAdapter.addTimeStamp(timeStamp);
        orderViewPager.setCurrentItem(0);
    }

    public void addOrder(DataSnapshot dataSnapshot) {
        GenericTypeIndicator objectMapType = new GenericTypeIndicator<HashMap<String, VoiceRecord>>() {};
        HashMap<String, VoiceRecord> objectMap = (HashMap) dataSnapshot.getValue(objectMapType);
        String key = dataSnapshot.getKey();
        String timeStamp = FileNameUtil.getTimeFromFileName(key);
        int position = orderViewPagerAdapter.addOrder(timeStamp, objectMap);
        if (position != -1) {
            orderViewPager.setCurrentItem(position, false);
        }
    }

    public void replaceAcceptedOrder(DataSnapshot dataSnapshot) {
        GenericTypeIndicator objectMapType = new GenericTypeIndicator<HashMap<String, VoiceRecord>>() {};
        HashMap<String, VoiceRecord> objectMap = (HashMap) dataSnapshot.getValue(objectMapType);
        String key = dataSnapshot.getKey();
        if (orderViewPagerAdapter != null) {
            String timeStamp = FileNameUtil.getTimeFromFileName(key);
            int position = orderViewPagerAdapter.replaceToAcceptedOrder(timeStamp, objectMap);
            orderViewPager.setCurrentItem(position, false);
        }
    }

    public void remove(String timeStamp) {
        orderViewPagerAdapter.remove(timeStamp);
    }

    public void setKeepScreenOn() {
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public void clearKeepScreenOn() {
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public void onStartRecording() {
        activity.onStartedRecording();
    }

    @Override
    public void onStopRecording() {
        activity.onStoppedRecording();
    }

}