package com.rena21c.voiceorder.activities;

import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.rena21c.voiceorder.App;
import com.rena21c.voiceorder.R;
import com.rena21c.voiceorder.etc.PreferenceManager;
import com.rena21c.voiceorder.view.actionbar.ActionBarViewModel;
import com.rena21c.voiceorder.view.components.OrderViewPagerLayout;
import com.rena21c.voiceorder.view.components.RecordGuideLayout;
import com.rena21c.voiceorder.view.components.RecordingLayout;
import com.rena21c.voiceorder.view.components.ReplaceableLayout;
import com.rena21c.voiceorder.view.dialogs.Dialogs;
import com.rena21c.voiceorder.view.widgets.RecordAndStopButton;

public class MainView implements RecordAndStopButton.activateRecorderListener {

    public static final int NO_INTERNAL_MEMORY = 0;
    public static final int NO_INTERNET_CONNECT = 1;

    private MainActivity activity;

    private ReplaceableLayout replaceableLayout;
    private RecordAndStopButton recordAndStopButton;
    private RecordingLayout recordingLayout;
    private OrderViewPagerLayout orderViewPagerLayout;

    public MainView(MainActivity activity) {
        this.activity = activity;
        initView();
    }

    private void initView() {
        ActionBarViewModel.createWithActionBar(activity.getApplicationContext(), activity.getSupportActionBar());

        replaceableLayout = (ReplaceableLayout) activity.findViewById(R.id.replaceableLayout);
        recordingLayout = RecordingLayout.getInstance(activity, replaceableLayout);
        orderViewPagerLayout = OrderViewPagerLayout.getInstance(activity, replaceableLayout);

        recordAndStopButton = (RecordAndStopButton) activity.findViewById(R.id.btnRecordAndStop);
        recordAndStopButton.setListener(this);

        if (PreferenceManager.getUserFirstVisit(activity)) {
            changeActionBarColorToYellow();
            recordAndStopButton.setInitHeight(recordAndStopButton.HEIGHT_WITH_GUIDE_LAYOUT);
            replaceableLayout.replaceChildView(RecordGuideLayout.getInstance(activity, replaceableLayout).getView());
        } else {
            recordAndStopButton.setInitHeight(recordAndStopButton.HEIGHT_WITH_ORDER_LIST_LAYOUT);
            replaceableLayout.replaceChildView(orderViewPagerLayout.getView());
        }
    }

    public void replaceViewToRecording() {
        replaceableLayout.replaceChildView(recordingLayout.getView());
        recordAndStopButton.setStopButton();
    }

    public void replaceViewToUnRecording() {
        replaceableLayout.replaceChildView(orderViewPagerLayout.getView());
        recordAndStopButton.setRecordButton();
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

    public void addOrderToViewPager(String fileName) {
        orderViewPagerLayout.addOrder(App.makeTimeFromFileName(fileName));
    }

    public void replaceAcceptedOrder(DataSnapshot dataSnapshot) {
        orderViewPagerLayout.replaceToAcceptedOrder(dataSnapshot);
    }

    public void replaceFailedOrder(String fileName) {
        orderViewPagerLayout.replaceToFailedOrder(fileName);
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
        activity.startedRecording();
    }

    @Override
    public void onStopRecording() {
        activity.stoppedRecording();
    }
}
