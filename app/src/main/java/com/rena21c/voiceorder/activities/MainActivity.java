package com.rena21c.voiceorder.activities;


import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.os.StatFs;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.rena21c.voiceorder.App;
import com.rena21c.voiceorder.R;
import com.rena21c.voiceorder.etc.PreferenceManager;
import com.rena21c.voiceorder.network.FileTransferUtil;
import com.rena21c.voiceorder.network.NetworkUtil;
import com.rena21c.voiceorder.view.actionbar.ActionBarViewModel;
import com.rena21c.voiceorder.view.components.OrderViewPagerLayout;
import com.rena21c.voiceorder.view.components.RecordGuideLayout;
import com.rena21c.voiceorder.view.components.RecordingLayout;
import com.rena21c.voiceorder.view.components.ReplaceableLayout;
import com.rena21c.voiceorder.view.dialogs.Dialogs;
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
    private PowerManager.WakeLock wakeLock;

    private long REQUIRED_SPACE = 5L * 1024L * 1024L;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("MainActivity", "OnCreate");
        setContentView(R.layout.activity_main);

        setChildEventListener(new ChildEventListener() {
            @Override public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.e("catchDataChanged", dataSnapshot.getKey());
                orderViewPagerLayout.replaceToAcceptedOrder(dataSnapshot);
            }
            @Override public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            @Override public void onChildRemoved(DataSnapshot dataSnapshot) {}
            @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override public void onCancelled(DatabaseError databaseError) {}
        });

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Recording");
        initView();
    }

    @Override protected void onStop() {
        super.onStop();
   
        if (wakeLock.isHeld()) {
            wakeLock.release();
            Log.e("MainActivity", "wakeLock.release in onStop()");
        }
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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
    public void onStartRecording() {
        if (!recordAndStopButton.isRecording()) {
            if (!wakeLock.isHeld()) {
                wakeLock.acquire(); // 유저가 강제로 화면을 끈 상태에서도 백그라운드에서  계속 작동하도록
                Log.e("MainActivity", "wakeLock.acquire in onStartRecording()");
            }
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            Log.d("MainActivity", getAvailableInternalMemorySize() + "");

            Log.d("", "free space" + getAvailableInternalMemorySize());
            if (getAvailableInternalMemorySize() < REQUIRED_SPACE) {
                Dialogs.showNoAvailableInternalMemoryDialog(this, null);
            } else {
                startRecording();
            }
        }
    }

    private void startRecording() {
        if (!NetworkUtil.isInternetConnected(getApplicationContext())) {
            Dialogs.showNoInternetConnectivityAlertDialog(this, null);
        } else {
            if (PreferenceManager.getUserFirstVisit(this)) {
                PreferenceManager.setUserFirstVisit(this);
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, android.R.color.white)));
            }

            time = System.currentTimeMillis();
            fileName = makeFileName();
            initRecorder(fileName);
            startRecord();

            replaceableLayout.replaceChildView(recordingLayout.getView());
            recordAndStopButton.setStopButton();
        }
    }

    @Override
    public void onStopRecording() {

        if (recordAndStopButton.isRecording()) {
            if (wakeLock.isHeld()) {
                wakeLock.release();
                Log.e("MainActivity", "wakeLock.release in onStopRecording()");
            }
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            stopRecord();

            upload(new TransferListener() {
                @Override
                public void onStateChanged(int id, TransferState state) {
                    if (state == TransferState.COMPLETED) {
                        PreferenceManager.setFileName(getApplicationContext(), fileName);
                        orderViewPagerLayout.addOrder(App.makeTimeFromFileName(fileName));
                        replaceableLayout.replaceChildView(orderViewPagerLayout.getView());
                        recordAndStopButton.setRecordButton();
                    } else {
                        if (state != TransferState.IN_PROGRESS) {
                            Toast.makeText(MainActivity.this, "파일 업로드시 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                            FirebaseCrash.logcat(Log.WARN, "NETWORK", "Aws s3 transfer state: " + state);
                        }
                    }
                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {}

                @Override
                public void onError(int id, Exception ex) {
                    Toast.makeText(MainActivity.this, "파일 업로드시 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                    FirebaseCrash.report(ex);
                }
            });
        }
    }

    private void upload(TransferListener transferListener) {
        final String BUCKET_NAME = getResources().getString(R.string.s3_bucket_name);
        File file = new File(getFilesDir().getPath() + "/" + fileName + ".mp4");
        TransferUtility transferUtility = FileTransferUtil.getTransferUtility(this);
        TransferObserver transferObserver = transferUtility.upload(BUCKET_NAME, file.getName(), file);
        transferObserver.setTransferListener(transferListener);
    }

    private long getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize;
        long availableBlocks;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            availableBlocks = stat.getAvailableBlocksLong();
            blockSize = stat.getBlockSizeLong();
        } else {
            availableBlocks = stat.getAvailableBlocks();
            blockSize = stat.getBlockSize();
        }
        return availableBlocks * blockSize;
    }

    private void setChildEventListener(ChildEventListener childEventListener) {
        String phoneNumber = PreferenceManager.setPhoneNumber(getApplicationContext());
        FirebaseDatabase.getInstance().getReference().child("orders")
                .child("restaurants")
                .orderByKey()
                .startAt(phoneNumber + "_00000000000000")
                .endAt(phoneNumber + "_99999999999999")
                .addChildEventListener(childEventListener);
    }
}
