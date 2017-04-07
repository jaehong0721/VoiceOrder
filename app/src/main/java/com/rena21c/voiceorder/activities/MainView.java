package com.rena21c.voiceorder.activities;

import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.rena21c.voiceorder.R;
import com.rena21c.voiceorder.firebase.FirebaseDbManager;
import com.rena21c.voiceorder.model.Order;
import com.rena21c.voiceorder.view.actionbar.ActionBarViewModel;
import com.rena21c.voiceorder.view.components.OrderViewPagerLayoutHolder;
import com.rena21c.voiceorder.view.components.ReplaceableLayout;
import com.rena21c.voiceorder.view.dialogs.Dialogs;
import com.rena21c.voiceorder.view.widgets.RecordAndStopButton;

import java.util.ArrayList;

public class MainView implements RecordAndStopButton.activateRecorderListener {

    public static final int NO_INTERNAL_MEMORY = 0;
    public static final int NO_INTERNET_CONNECT = 1;


    private MainActivity activity;

    private ReplaceableLayout replaceableLayout;
    private RecordAndStopButton recordAndStopButton;
    private View recordingLayout;
    private OrderViewPagerLayoutHolder orderViewPagerLayoutHolder;

    public MainView(MainActivity activity) {
        this.activity = activity;
        ActionBarViewModel.createWithActionBar(activity.getApplicationContext(), activity.getSupportActionBar());
        replaceableLayout = (ReplaceableLayout) activity.findViewById(R.id.replaceableLayout);
        recordingLayout = activity.getLayoutInflater().inflate(R.layout.layout_component_recording, replaceableLayout, false);

        recordAndStopButton = (RecordAndStopButton) activity.findViewById(R.id.btnRecordAndStop);
        recordAndStopButton.setListener(this);
    }


    public void initView(boolean shouldHoswGuide, FirebaseDbManager dbManager, ArrayList<Order> orders) {
        orderViewPagerLayoutHolder = new OrderViewPagerLayoutHolder(activity, replaceableLayout, dbManager, orders);
        if (shouldHoswGuide) {
            setGuide();
        } else {
            setNormal();
        }
    }

    private void setGuide() {
        changeActionBarColorToYellow();
        recordAndStopButton.setInitHeight(recordAndStopButton.HEIGHT_WITH_GUIDE_LAYOUT);
        View recordGuideLayout = activity.getLayoutInflater().inflate(R.layout.layout_component_record_guide, replaceableLayout, false);
        replaceableLayout.replaceChildView(recordGuideLayout);
    }

    private void setNormal() {
        recordAndStopButton.setInitHeight(recordAndStopButton.HEIGHT_WITH_ORDER_LIST_LAYOUT);
        replaceableLayout.replaceChildView(orderViewPagerLayoutHolder.getView());
    }

    public void replaceViewToRecording() {
        replaceableLayout.replaceChildView(recordingLayout);
        recordAndStopButton.setStopViewState();
    }

    public void replaceViewToUnRecording() {
        replaceableLayout.replaceChildView(orderViewPagerLayoutHolder.getView());
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

    public void addOrderToViewPager(String timeStamp) {
        orderViewPagerLayoutHolder.addOrder(timeStamp);
    }

    public void replaceAcceptedOrder(DataSnapshot dataSnapshot) {
        orderViewPagerLayoutHolder.replaceToAcceptedOrder(dataSnapshot);
    }

    public void replaceFailedOrder(String fileName) {
        orderViewPagerLayoutHolder.replaceToFailedOrder(fileName);
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
