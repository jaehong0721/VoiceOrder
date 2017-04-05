package com.rena21c.voiceorder.activities;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
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
import com.rena21c.voiceorder.firebase.FirebaseDbManager;
import com.rena21c.voiceorder.network.FileTransferUtil;
import com.rena21c.voiceorder.network.NetworkUtil;
import com.rena21c.voiceorder.services.AwsS3FileUploader;
import com.rena21c.voiceorder.services.VoiceRecorderManager;
import com.rena21c.voiceorder.util.FileNameUtil;
import com.rena21c.voiceorder.util.MemorySizeChecker;

import java.io.File;

public class MainActivity extends BaseActivity implements VoiceRecorderManager.VoiceRecordCallback {

    private MainView mainView;

    private boolean isUploading;
    private AppPreferenceManager appPreferenceManager;

    private VoiceRecorderManager recordManager;
    private FirebaseDbManager dbManager;

    private final long REQUIRED_SPACE = 5L * 1024L * 1024L;
    private MemorySizeChecker memorySizeChecker;
    private AwsS3FileUploader fileUploader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("MainActivity", "OnCreate");
        setContentView(R.layout.activity_main);

        memorySizeChecker = new MemorySizeChecker(REQUIRED_SPACE);

        dbManager = new FirebaseDbManager(FirebaseDatabase.getInstance());

        fileUploader = new AwsS3FileUploader.Builder()
                .setBucketName(getResources().getString(R.string.s3_bucket_name))
                .setTransferUtility(FileTransferUtil.getTransferUtility(this))
                .build();

        appPreferenceManager = App.getApplication(getApplicationContext()).getPreferenceManager();
        mainView = new MainView(MainActivity.this, appPreferenceManager);

        recordManager = new VoiceRecorderManager(getFilesDir().getPath(), this);

        dbManager.subscribeAcceptedOrder(appPreferenceManager.getPhoneNumber(), new ChildEventListener() {
            @Override public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                mainView.replaceAcceptedOrder(dataSnapshot);
            }

            @Override public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                mainView.replaceAcceptedOrder(dataSnapshot);
            }

            @Override public void onChildRemoved(DataSnapshot dataSnapshot) {
                mainView.replaceAcceptedOrder(dataSnapshot);
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
        if (memorySizeChecker.isEnough()) {
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
        File file = new File(getFilesDir().getPath() + "/" + fileName + ".mp4");
        fileUploader.upload(file, new TransferListener() {
            @Override public void onStateChanged(int id, TransferState state) {
                if (state == TransferState.COMPLETED) {
                    Log.d("s3 upload", "s3 state :" + state);
                    storeFileName(fileName);
                } else if (state == TransferState.WAITING_FOR_NETWORK) {
                    Log.d("s3 upload", "s3 state :" + state);
                    mainView.showToastWaitingForNetwork();
                } else if (state == TransferState.FAILED) {
                    Log.d("s3 upload", "s3 state :" + state);
                    mainView.replaceFailedOrder(fileName);
                    isUploading = false;
                    FirebaseCrash.logcat(Log.WARN, "NETWORK", "Aws s3 transfer state: " + state);
                } else {
                    Log.d("s3 upload", "s3 state :" + state);
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

    private void storeFileName(final String fileName) {
        dbManager.addFileName(appPreferenceManager.getPhoneNumber(), fileName, new OnCompleteListener() {
            @Override public void onComplete(@NonNull Task task) {
                new OnCompleteListener<Void>() {
                    @Override public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()) {
                            mainView.replaceFailedOrder(fileName);
                            //s3에 올라간 파일 삭제?
                            FirebaseCrash.report(task.getException());
                        }
                        isUploading = false;
                    }
                };
            }
        });

    }


}
