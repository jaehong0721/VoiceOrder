package com.rena21c.voiceorder.activities;


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
import com.rena21c.voiceorder.App;
import com.rena21c.voiceorder.R;
import com.rena21c.voiceorder.etc.AppPreferenceManager;
import com.rena21c.voiceorder.network.FileTransferUtil;
import com.rena21c.voiceorder.network.NetworkUtil;
import com.rena21c.voiceorder.services.VoiceRecorderManager;
import com.rena21c.voiceorder.util.FileNameUtil;

import java.io.File;

public class MainActivity extends BaseActivity implements VoiceRecorderManager.VoiceRecordCallback {

    private MainView mainView;

    private final long REQUIRED_SPACE = 5L * 1024L * 1024L;
    private boolean isUploading;
    private AppPreferenceManager appPreferenceManager;

    private VoiceRecorderManager recordManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("MainActivity", "OnCreate");
        setContentView(R.layout.activity_main);

        appPreferenceManager = App.getApplication(getApplicationContext()).getPreferenceManager();
        mainView = new MainView(MainActivity.this, appPreferenceManager);

        recordManager = new VoiceRecorderManager(getFilesDir().getPath(), this);

        setAcceptedOrderEventListener(new ChildEventListener() {
            @Override public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                mainView.replaceAcceptedOrder(dataSnapshot);
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
        recordManager.stop();
        mainView.clearKeepScreenOn();
        mainView.replaceViewToUnRecording();
    }

    @Override public void onStartRecord() {
        mainView.replaceViewToRecording();
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
                if (appPreferenceManager.getUserFirstVisit()) {
                    appPreferenceManager.setUserFirstVisit();
                    mainView.changeActionBarColorToWhite();
                }
                String fileName = FileNameUtil.makeFileName(appPreferenceManager.getPhoneNumber(), System.currentTimeMillis());
                recordManager.start(fileName);
            }
        }
    }


    public void stoppedRecording() {
        final String fileName = recordManager.stop();
        mainView.clearKeepScreenOn();

        mainView.addOrderToViewPager(fileName);
        mainView.replaceViewToUnRecording();

        isUploading = true;
        upload(fileName, new TransferListener() {
            @Override public void onStateChanged(int id, TransferState state) {
                if (state == TransferState.COMPLETED) {
                    Log.e("s3 upload", "s3 state :" + state);
                    storeFileName(fileName);
                } else if (state == TransferState.WAITING_FOR_NETWORK) {
                    Log.e("s3 upload", "s3 state :" + state);
                    mainView.showToastWaitingForNetwork();
                } else if (state == TransferState.FAILED) {
                    Log.e("s3 upload", "s3 state :" + state);
                    mainView.replaceFailedOrder(fileName);
                    isUploading = false;
                    FirebaseCrash.logcat(Log.WARN, "NETWORK", "Aws s3 transfer state: " + state);
                } else {
                    Log.e("s3 upload", "s3 state :" + state);
                }
            }

            @Override public void onError(int id, Exception ex) {
                mainView.replaceFailedOrder(fileName);
                isUploading = false;
                FirebaseCrash.report(ex);
            }

            @Override public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {}
        });
    }

    private void upload(String fileName, TransferListener transferListener) {
        final String BUCKET_NAME = getResources().getString(R.string.s3_bucket_name);
        File file = new File(getFilesDir().getPath() + "/" + fileName + ".mp4");
        TransferUtility transferUtility = FileTransferUtil.getTransferUtility(this);
        TransferObserver transferObserver = transferUtility.upload(BUCKET_NAME, file.getName(), file);
        transferObserver.setTransferListener(transferListener);
    }

    private void storeFileName(final String fileName) {
        FirebaseDatabase.getInstance().getReference().child("restaurants")
                .child(appPreferenceManager.getPhoneNumber())
                .child("recordedOrders")
                .push()
                .child("fileName")
                .setValue(fileName)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()) {
                            mainView.replaceFailedOrder(fileName);
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
                .startAt(appPreferenceManager.getPhoneNumber() + "_00000000000000")
                .endAt(appPreferenceManager.getPhoneNumber() + "_99999999999999")
                .addChildEventListener(childEventListener);
    }

}
