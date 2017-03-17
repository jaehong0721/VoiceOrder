package com.rena21c.voiceorder.activities;

import android.graphics.drawable.ColorDrawable;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.rena21c.voiceorder.App;
import com.rena21c.voiceorder.R;
import com.rena21c.voiceorder.etc.PreferenceManager;
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

public class MainActivity extends BaseActivity implements RecordAndStopButton.activateRecorderListener {

    private ReplaceableLayout replaceableLayout;
    private RecordAndStopButton recordAndStopButton;

    private MediaRecorder recorder;
    private String fileName;
    private long time;

    private RecordingLayout recordingLayout;
    private OrderViewPagerLayout orderViewPagerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        ActionBarViewModel.createWithActionBar(getApplicationContext(), getSupportActionBar());

        replaceableLayout = (ReplaceableLayout) findViewById(R.id.replaceableLayout);
        recordingLayout = RecordingLayout.getInstance(this, replaceableLayout);
        orderViewPagerLayout = OrderViewPagerLayout.getInstance(this, replaceableLayout);

        recordAndStopButton = (RecordAndStopButton) findViewById(R.id.btnRecordAndStop);
        recordAndStopButton.setListener(this);

        if (PreferenceManager.getUserFirstVisit(this)) {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.primaryYellow)));
            recordAndStopButton.setInitHeight(recordAndStopButton.HEIGHT_WITH_GUIDE_LAYOUT);
            replaceableLayout.replaceChildView(RecordGuideLayout.getInstance(this, replaceableLayout).getView());
        } else {
            recordAndStopButton.setInitHeight(recordAndStopButton.HEIGHT_WITH_ORDER_LIST_LAYOUT);
            replaceableLayout.replaceChildView(orderViewPagerLayout.getView());
        }
    }

    private void initRecorder(String fileName) {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        recorder.setAudioSamplingRate(44100);
        recorder.setAudioEncodingBitRate(128000);
        recorder.setOutputFile(getFilesDir().getPath() + "/" + fileName + ".mp4");
    }

    private String makeFileName() {
        SimpleDateFormat dayTime = new SimpleDateFormat("yyyyMMddHHmmss");
        String date = dayTime.format(new Date(time));
        fileName = PreferenceManager.getPhoneNumber(getApplicationContext()) + "_" + date;
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
        if (recorder != null) {
            recorder.stop();
            recorder.release();
            recorder = null;
        }
    }

    @Override
    public void record() {

        time = System.currentTimeMillis();

        fileName = makeFileName();

        initRecorder(fileName);
        startRecord();

        if (PreferenceManager.getUserFirstVisit(this)) {
            PreferenceManager.setUserFirstVisit(this);
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, android.R.color.transparent)));
        }
        replaceableLayout.replaceChildView(recordingLayout.getView());
    }

    @Override
    public void stop() {

        stopRecord();

        upload(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                if (state == TransferState.COMPLETED) {
                    PreferenceManager.setFileName(getApplicationContext(),fileName);
                    orderViewPagerLayout.addOrder(((App)getApplication()).makeTimeFromFileName(fileName));
                    replaceableLayout.replaceChildView(orderViewPagerLayout.getView());
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
            }

            @Override
            public void onError(int id, Exception ex) {
            }
        });
    }

    private void upload(TransferListener transferListener) {
        final String BUCKET_NAME = "tgmorders";
        File file = new File(getFilesDir().getPath() + "/" + fileName + ".mp4");
        TransferUtility transferUtility = FileTransferUtil.getTransferUtility(this);
        TransferObserver transferObserver = transferUtility.upload(BUCKET_NAME, file.getName(), file);
        transferObserver.setTransferListener(transferListener);

    }
}
