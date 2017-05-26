package com.rena21c.voiceorder.activities;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.GenericTypeIndicator;
import com.rena21c.voiceorder.R;
import com.rena21c.voiceorder.firebase.FirebaseDbManager;
import com.rena21c.voiceorder.model.VoiceRecord;
import com.rena21c.voiceorder.util.FileNameUtil;
import com.rena21c.voiceorder.view.actionbar.ActionBarOnMain;
import com.rena21c.voiceorder.view.adapters.OrderViewPagerAdapter;
import com.rena21c.voiceorder.view.adapters.SimpleViewPagerSelectedListener;
import com.rena21c.voiceorder.view.components.ReplaceableLayout;
import com.rena21c.voiceorder.view.dialogs.Dialogs;
import com.rena21c.voiceorder.view.widgets.RecordAndStopButton;
import com.rena21c.voiceorder.view.widgets.ViewPagerIndicator;

import java.util.HashMap;

public class MainView implements RecordAndStopButton.activateRecorderListener {

    public static final int NO_INTERNAL_MEMORY = 0;
    public static final int NO_INTERNET_CONNECT = 1;

    private MainActivity activity;

    private ReplaceableLayout replaceableLayout;
    private RecordAndStopButton recordAndStopButton;
    private View recordingLayout;

    private OrderViewPagerAdapter orderViewPagerAdapter;
    private ViewPager orderViewPager;
    private ViewPagerIndicator viewPagerIndicator;
    private View viewPager;

    public MainView(MainActivity activity) {
        this.activity = activity;

        ActionBarOnMain
                .createWithActionBar(activity.getApplicationContext(), activity.getSupportActionBar())
                .setInitialTab();

        replaceableLayout = (ReplaceableLayout) activity.findViewById(R.id.replaceableLayout);
        recordingLayout = activity.getLayoutInflater().inflate(R.layout.layout_component_recording, replaceableLayout, false);

        recordAndStopButton = (RecordAndStopButton) activity.findViewById(R.id.btnRecordAndStop);
        recordAndStopButton.setListener(this);
    }

    public void initView(boolean shouldShowGuide, FirebaseDbManager dbManager) {

        LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        viewPager = layoutInflater.inflate(R.layout.layout_component_order_view_pager, replaceableLayout, false);

        viewPagerIndicator = (ViewPagerIndicator) viewPager.findViewById(R.id.viewPagerIndicator);
        orderViewPager = (ViewPager) viewPager.findViewById(R.id.viewPager);

        orderViewPagerAdapter = new OrderViewPagerAdapter(activity, dbManager, new OrderViewPagerAdapter.ItemCountChangedListener() {
            @Override public void itemCountChange(int count) {
                viewPagerIndicator.createDot(count);
                if (count == 1) viewPagerIndicator.selectDot(0);
            }
        });

        orderViewPager.setAdapter(orderViewPagerAdapter);
        orderViewPager.addOnPageChangeListener(new SimpleViewPagerSelectedListener() {
            @Override public void onPageSelected(int position) {
                viewPagerIndicator.selectDot(position);
            }
        });

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
        changeActionBarColorToYellow();
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

    public void changeActionBarColorToWhite() {
        activity.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(activity, android.R.color.white)));
    }

    public void changeActionBarColorToYellow() {
        activity.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(activity, R.color.primaryYellow)));
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

    public void replaceFailedOrder(String fileName) {
        String timeStamp = FileNameUtil.getTimeFromFileName(fileName);
        int position = orderViewPagerAdapter.replaceToFailedOrder(timeStamp);
        orderViewPager.setCurrentItem(position);
    }

    public void remove(String timeStamp) {
        orderViewPagerAdapter.remove(timeStamp);
    }

    public void showToastIsUploading() {
        Toast.makeText(activity, "주문 전송 중입니다. 잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
    }

    public void showToastUploadError() {
        Toast.makeText(activity, "파일 업로드시 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
    }

    public void showToastWaitingForNetwork() {
        Toast.makeText(activity, "네트워크 접속이 끊겼습니다. 네트워크 접속을 활성화해주세요", Toast.LENGTH_SHORT).show();
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
