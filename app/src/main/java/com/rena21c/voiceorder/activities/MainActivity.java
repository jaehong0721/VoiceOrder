package com.rena21c.voiceorder.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.TelephonyManager;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.rena21c.voiceorder.R;
import com.rena21c.voiceorder.network.FileTransferUtil;
import com.rena21c.voiceorder.view.actionbar.ActionBarViewModel;
import com.rena21c.voiceorder.view.components.OrderViewPagerLayout;
import com.rena21c.voiceorder.view.components.RecordGuideLayout;
import com.rena21c.voiceorder.view.components.RecordingLayout;
import com.rena21c.voiceorder.view.components.ReplaceableLayout;
import com.rena21c.voiceorder.view.widgets.RecordAndStopButton;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends BaseActivity implements RecordAndStopButton.activateRecorderListener{

    private ReplaceableLayout replaceableLayout;
    private RecordAndStopButton recordAndStopButton;

    private MediaRecorder recorder;
    private boolean isRecord;
    private String fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        if(checkFirstRun()) {
            recordAndStopButton.setInitHeight(recordAndStopButton.HEIGHT_WITH_GUIDE_LAYOUT);
            replaceableLayout.replaceChildView(RecordGuideLayout.getInstance(this, replaceableLayout).getView());
        }
        else {
            recordAndStopButton.setInitHeight(recordAndStopButton.HEIGHT_WITH_ORDER_LIST_LAYOUT);
            replaceableLayout.replaceChildView(OrderViewPagerLayout.getInstance(this, replaceableLayout).getView());
        }
    }

    private boolean checkFirstRun() {
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        boolean isFirst = sharedPreferences.getBoolean("isFirst", true);
        if(isFirst) {
            sharedPreferences.edit().putBoolean("isFirst", false).commit();
            return true;
        }
        else {
            return false;
        }
    }

    private void initView() {
        ActionBarViewModel.createWithActionBar(getApplicationContext(), getSupportActionBar());

        replaceableLayout = (ReplaceableLayout)findViewById(R.id.replaceableLayout);
        recordAndStopButton = (RecordAndStopButton)findViewById(R.id.btnRecordAndStop);
        recordAndStopButton.setListener(this);
    }

    private void initRecorder(String fileName) {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        recorder.setOutputFile(Environment.getExternalStorageDirectory().getPath() + "/" + fileName);
    }

    private String setFileName() {
        String phoneNumber = ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).getLine1Number();
        if(phoneNumber.substring(0,3).equals("+82")) {
            phoneNumber = phoneNumber.replace("+82", "0");
        }
        long time = System.currentTimeMillis();
        SimpleDateFormat dayTime = new SimpleDateFormat("yyyyMMddHHmmss");
        String date = dayTime.format(new Date(time));
        fileName = phoneNumber + "_" + date + ".mp4";
        return fileName;
    }
    private void startRecord() {

        try {
            recorder.prepare();
            recorder.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopRecord() {
        if(recorder != null) {
            recorder.stop();
            recorder.reset();
            recorder.release();
            recorder = null;
        }
    }

    @Override
    protected void onPause() {
        if(isRecord) {

        }
        super.onPause();
    }

    @Override
    public void record() {
        String fileName = setFileName();
        initRecorder(fileName);
        startRecord();
        isRecord = true;
        replaceableLayout.replaceChildView(RecordingLayout.getInstance(this, replaceableLayout).getView());
    }

    @Override
    public void stop() {
        stopRecord();
        upload();
        isRecord = false;
        replaceableLayout.replaceChildView(OrderViewPagerLayout.getInstance(this, replaceableLayout).getView());
    }

    private void upload() {
        final String BUCKET_NAME = "tgmorders";
        File file = new File(Environment.getExternalStorageDirectory().getPath() + "/" + fileName);
        if(file.isFile()) {
            TransferUtility transferUtility = FileTransferUtil.getTransferUtility(this);
            transferUtility.upload(BUCKET_NAME, file.getName(), file);
        }
    }
}
