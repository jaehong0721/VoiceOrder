package com.rena21c.voiceorder.activities;


import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.support.annotation.NonNull;
import android.util.Log;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.rena21c.voiceorder.R;
import com.rena21c.voiceorder.etc.PreferenceManager;
import com.rena21c.voiceorder.network.FileTransferUtil;
import com.rena21c.voiceorder.network.NetworkUtil;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends BaseActivity {

    private MainView mainView;
    private MediaRecorder recorder;
    private String fileName;
    private String phoneNumber;

    private final long REQUIRED_SPACE = 5L * 1024L * 1024L;
    private boolean isUploading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("MainActivity", "OnCreate");
        setContentView(R.layout.activity_main);

        mainView = new MainView(MainActivity.this);
        phoneNumber = PreferenceManager.setPhoneNumber(getApplicationContext());

        setAcceptedOrderEventListener(new ChildEventListener() {
            @Override public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                mainView.acceptedOrder(dataSnapshot);
            }
            @Override public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.e("onChildChanged", dataSnapshot.getKey());
            }
            @Override public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.e("onChildRemoved", dataSnapshot.getKey());
            }
            @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override public void onCancelled(DatabaseError databaseError) {}
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopRecord();
        mainView.clearKeepScreenOn();
        mainView.replaceViewToUnRecording();
    }

    public void startedRecording() {
        if (getAvailableInternalMemorySize() < REQUIRED_SPACE) {
            mainView.showDialog(MainView.NO_INTERNAL_MEMORY);
        } else {
            mainView.setKeepScreenOn();
            startRecording();
        }
    }

    private void startRecording() {
        if (isUploading) {
            mainView.showToastIsUploading();
        } else {
            if (!NetworkUtil.isInternetConnected(getApplicationContext())) {
                mainView.showDialog(MainView.NO_INTERNET_CONNECT);
            } else {
                if (PreferenceManager.getUserFirstVisit(this)) {
                    PreferenceManager.setUserFirstVisit(this);
                    mainView.changeActionBarColorToWhite();
                }
                fileName = makeFileName(System.currentTimeMillis());
                initRecorder(fileName);
                startRecord();
            }
        }
    }

    private String makeFileName(long time) {
        SimpleDateFormat dayTime = new SimpleDateFormat("yyyyMMddHHmmss");
        String date = dayTime.format(new Date(time));
        fileName = PreferenceManager.getPhoneNumber(getApplicationContext()) + "_" + date;
        return fileName;
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

    private void startRecord() {
        try {
            recorder.prepare();
            recorder.start();
            mainView.replaceViewToRecording();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stoppedRecording() {
        stopRecord();
        mainView.clearKeepScreenOn();
        mainView.replaceViewToUnRecording();

        isUploading = true;
        upload(new TransferListener() {
            @Override public void onStateChanged(int id, TransferState state) {
                if (state == TransferState.COMPLETED) {
                    Log.e("s3 upload", "s3 state :" + state);
                    storeFileName();
                } else if(state == TransferState.WAITING_FOR_NETWORK) {
                    Log.e("s3 upload", "s3 state :" + state);
                    mainView.showToastWatingForNetwork();
                } else if(state == TransferState.FAILED) {
                    Log.e("s3 upload", "s3 state :" + state);
                    mainView.showToastUploadError();
                    isUploading = false;
                    FirebaseCrash.logcat(Log.WARN, "NETWORK", "Aws s3 transfer state: " + state);
                } else {
                    Log.e("s3 upload", "s3 state :" + state);
                }
            }
            @Override public void onError(int id, Exception ex) {
                mainView.showToastUploadError();
                isUploading = false;
                FirebaseCrash.report(ex);
            }
            @Override public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {}
        });

    }

    private void stopRecord() {
        if (recorder != null) {
            recorder.stop();
            recorder.release();
            recorder = null;
        }
    }

    private void upload(TransferListener transferListener) {
        final String BUCKET_NAME = getResources().getString(R.string.s3_bucket_name);
        File file = new File(getFilesDir().getPath() + "/" + fileName + ".mp4");
        TransferUtility transferUtility = FileTransferUtil.getTransferUtility(this);
        TransferObserver transferObserver = transferUtility.upload(BUCKET_NAME, file.getName(), file);
        transferObserver.setTransferListener(transferListener);
    }

    private void storeFileName() {
        FirebaseDatabase.getInstance().getReference().child("restaurants")
                .child(phoneNumber)
                .child("recordedOrders")
                .push()
                .child("fileName")
                .setValue(fileName)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            mainView.addOrderToViewPager(fileName);
                        } else {
                            mainView.showToastUploadError();
                            //s3에 올라간 파일 삭제?
                            FirebaseCrash.report(task.getException());
                        }
                        isUploading = false;
                    }
                });
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

    private void setAcceptedOrderEventListener(ChildEventListener childEventListener) {
        FirebaseDatabase.getInstance().getReference().child("orders")
                .child("restaurants")
                .orderByKey()
                .startAt(phoneNumber + "_00000000000000")
                .endAt(phoneNumber + "_99999999999999")
                .addChildEventListener(childEventListener);
    }
}
