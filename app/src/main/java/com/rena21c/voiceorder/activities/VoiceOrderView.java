package com.rena21c.voiceorder.activities;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.GenericTypeIndicator;
import com.rena21c.voiceorder.R;
import com.rena21c.voiceorder.etc.RecordedFileManager;
import com.rena21c.voiceorder.firebase.FirebaseDbManager;
import com.rena21c.voiceorder.model.VoiceRecord;
import com.rena21c.voiceorder.util.FileNameUtil;
import com.rena21c.voiceorder.view.adapters.OrderViewPagerAdapter;
import com.rena21c.voiceorder.view.adapters.SimpleViewPagerSelectedListener;
import com.rena21c.voiceorder.view.components.ReplaceableLayout;
import com.rena21c.voiceorder.view.dialogs.Dialogs;
import com.rena21c.voiceorder.view.widgets.InquireByCallButton;
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
    private View orderLayout;
    private View recordGuideLayout;

    private InquireByCallButton callButton;
    private ImageView ivPlayTutorialVideo;

    public VoiceOrderView(final VoiceOrderActivity activity, FirebaseDbManager dbManager, RecordedFileManager recordedFileManager) {
        this.activity = activity;

        LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        callButton = (InquireByCallButton) activity.findViewById(R.id.btnInquire);

        recordAndStopButton = (RecordAndStopButton) activity.findViewById(R.id.btnRecordAndStop);
        recordAndStopButton.setListener(this);

        replaceableLayout = (ReplaceableLayout) activity.findViewById(R.id.replaceableLayout);

        recordingLayout = layoutInflater.inflate(R.layout.layout_component_recording, replaceableLayout, false);

        recordGuideLayout = layoutInflater.inflate(R.layout.layout_component_record_guide, replaceableLayout, false);
        ivPlayTutorialVideo = (ImageView) recordGuideLayout.findViewById(R.id.ivPlayTutorialVideo);
        ivPlayTutorialVideo.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                activity.playTutorialVideo();
            }
        });

        orderLayout = layoutInflater.inflate(R.layout.layout_component_order_view_pager, replaceableLayout, false);
        viewPagerIndicator = (ViewPagerIndicator) orderLayout.findViewById(R.id.viewPagerIndicator);
        orderViewPager = (ViewPager) orderLayout.findViewById(R.id.viewPager);

        orderViewPagerAdapter = new OrderViewPagerAdapter(activity, dbManager, recordedFileManager, new OrderViewPagerAdapter.ItemCountChangedListener() {
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
        int position = orderViewPagerAdapter.addTimeStamp(fileName);
        orderViewPager.setCurrentItem(position);
    }

    private void setGuide() {
        recordAndStopButton.setInitHeight(recordAndStopButton.HEIGHT_WITH_GUIDE_LAYOUT);
        callButton.setVisibility(View.VISIBLE);
        replaceableLayout.replaceChildView(recordGuideLayout);
    }

    private void setNormal() {
        recordAndStopButton.setInitHeight(recordAndStopButton.HEIGHT_WITH_ORDER_LIST_LAYOUT);
        callButton.setVisibility(View.GONE);
        replaceableLayout.replaceChildView(orderLayout);
    }

    public void replaceViewToRecording() {
        if(callButton.getVisibility() == View.VISIBLE) callButton.setVisibility(View.GONE);
        replaceableLayout.replaceChildView(recordingLayout);
        recordAndStopButton.setStopViewState();
    }

    public void replaceViewToUnRecording() {
        replaceableLayout.replaceChildView(orderLayout);
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

    public void addOrder(String phoeNumber, DataSnapshot dataSnapshot) {
        GenericTypeIndicator objectMapType = new GenericTypeIndicator<HashMap<String, VoiceRecord>>() {};
        HashMap<String, VoiceRecord> objectMap = (HashMap) dataSnapshot.getValue(objectMapType);
        String key = dataSnapshot.getKey();
        String timeStamp = FileNameUtil.getTimeFromFileName(key);
        int position = orderViewPagerAdapter.addOrder(phoeNumber, timeStamp, objectMap);
        if (position != -1) {
            orderViewPager.setCurrentItem(position, false);
        }
    }

    public void replaceAcceptedOrder(String phoneNumber, DataSnapshot dataSnapshot) {
        GenericTypeIndicator objectMapType = new GenericTypeIndicator<HashMap<String, VoiceRecord>>() {};
        HashMap<String, VoiceRecord> objectMap = (HashMap) dataSnapshot.getValue(objectMapType);
        String key = dataSnapshot.getKey();
        if (orderViewPagerAdapter != null) {
            String timeStamp = FileNameUtil.getTimeFromFileName(key);
            int position = orderViewPagerAdapter.replaceToAcceptedOrder(phoneNumber,timeStamp, objectMap);
            orderViewPager.setCurrentItem(position, false);
        }
    }


    public void removeOrder(DataSnapshot dataSnapshot) {
        String timeStamp = FileNameUtil.getTimeFromFileName(dataSnapshot.getKey());
        orderViewPagerAdapter.removeOrder(timeStamp);
    }

    public void removeTimeStamp(String timeStamp) {
        orderViewPagerAdapter.removeTimeStamp(timeStamp);
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
