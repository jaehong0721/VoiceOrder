package com.rena21c.voiceorder.activities;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.rena21c.voiceorder.App;
import com.rena21c.voiceorder.R;
import com.rena21c.voiceorder.etc.AppPreferenceManager;
import com.rena21c.voiceorder.firebase.FirebaseDbManager;
import com.rena21c.voiceorder.firebase.ToastErrorHandlingListener;
import com.rena21c.voiceorder.network.FileTransferUtil;
import com.rena21c.voiceorder.network.NetworkUtil;
import com.rena21c.voiceorder.services.AwsS3FileUploader;
import com.rena21c.voiceorder.services.FileUploadService;
import com.rena21c.voiceorder.services.VoiceRecorderManager;
import com.rena21c.voiceorder.util.FileNameUtil;
import com.rena21c.voiceorder.util.MemorySizeChecker;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends HasTabActivity implements VoiceRecorderManager.VoiceRecordCallback {

    private final long REQUIRED_SPACE = 5L * 1024L * 1024L;

    private AppPreferenceManager appPreferenceManager;
    private VoiceRecorderManager recordManager;
    private FirebaseDbManager dbManager;
    private MemorySizeChecker memorySizeChecker;
    private AwsS3FileUploader fileUploader;
    private ChildEventListener acceptedOrderChildEventListener;

    private MainView mainView;

    private Query acceptedOrderQuery;
    private BroadcastReceiver fileUploadSuccessReceiver;
    private ChildEventListener recordListListener;
    private Query recordListQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("MainActivity", "OnCreate");
        setTheme(R.style.MainTheme);
        setContentView(R.layout.activity_main);

        fileUploadSuccessReceiver = new BroadcastReceiver() {
            @Override public void onReceive(Context context, Intent intent) {
                String file = intent.getStringExtra("file");
                boolean success = intent.getBooleanExtra("success", false);
                Log.d("MainActivity", "파일 전송 완료, 파일: " + file + ", 성공: " + success);
            }
        };

        mainView = new MainView(MainActivity.this);
        appPreferenceManager = App.getApplication(getApplicationContext()).getPreferenceManager();
        dbManager = new FirebaseDbManager(FirebaseDatabase.getInstance());

        mainView.initView(dbManager);

        recordManager = new VoiceRecorderManager(getFilesDir().getPath(), this);
        memorySizeChecker = new MemorySizeChecker(REQUIRED_SPACE);
        fileUploader = new AwsS3FileUploader.Builder()
                .setBucketName(getResources().getString(R.string.s3_bucket_name))
                .setTransferUtility(FileTransferUtil.getTransferUtility(this))
                .build();

        acceptedOrderChildEventListener = new ChildEventListener() {
            @Override public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d("DB", "added: " + dataSnapshot.toString());
                mainView.addOrder(dataSnapshot);
            }

            @Override public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d("DB", "changed: " + dataSnapshot.toString());
                mainView.replaceAcceptedOrder(dataSnapshot);
            }

            @Override public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d("DB", "removed: " + dataSnapshot.toString());
                mainView.replaceAcceptedOrder(dataSnapshot);
            }

            @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

            @Override public void onCancelled(DatabaseError databaseError) {}
        };


        acceptedOrderQuery = dbManager.subscribeAcceptedOrder(appPreferenceManager.getPhoneNumber(), acceptedOrderChildEventListener);

        // TODO: 구독방식으로 리팩토링
        dbManager.getRecordedOrder(appPreferenceManager.getPhoneNumber(), new ToastErrorHandlingListener(this) {
            @Override public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> fileNameList = getFileNameListFrom(dataSnapshot.getChildren().iterator());
                for (String fileName : fileNameList) {
                    mainView.addTimeStamp(fileName);
                }
            }
        });

        recordListListener = new ChildEventListener() {
            @Override public void onChildAdded(DataSnapshot dataSnapshot, String s) { }

            @Override public void onChildChanged(DataSnapshot dataSnapshot, String s) { }

            @Override public void onChildRemoved(DataSnapshot dataSnapshot) {
                String timeStamp = FileNameUtil.getTimeFromFileName(dataSnapshot.getKey());
                mainView.remove(timeStamp);
            }

            @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) { }

            @Override public void onCancelled(DatabaseError databaseError) { }
        };

        recordListQuery = dbManager.subscribeRecordedOrder(appPreferenceManager.getPhoneNumber(), recordListListener);

    }

    @Override protected void onDestroy() {
        super.onDestroy();
        acceptedOrderQuery.removeEventListener(acceptedOrderChildEventListener);
        recordListQuery.removeEventListener(recordListListener);
    }

    @Override protected void onStart() {
        super.onStart();
        registerReceiver(fileUploadSuccessReceiver, new IntentFilter("com.rena21c.voiceorder.ACTION_UPLOAD"));
        mainView.setView(appPreferenceManager.getUserFirstVisit());
    }

    @Override
    protected void onStop() {
        super.onStop();
        recordManager.cancel();
        mainView.clearKeepScreenOn();
        mainView.replaceViewToUnRecording();
        unregisterReceiver(fileUploadSuccessReceiver);
    }

    @Override public void onStartRecord() {
        mainView.replaceViewToRecording();
    }

    public void onStartedRecording() {
        if (memorySizeChecker.isEnough()) {
            mainView.showDialog(MainView.NO_INTERNAL_MEMORY);
        } else {
            mainView.setKeepScreenOn();
            startRecording();
        }
    }

    public void onStoppedRecording() {
        final String fileName = recordManager.stop();
        mainView.clearKeepScreenOn();
        mainView.addEmptyOrderToViewPager(FileNameUtil.getTimeFromFileName(fileName));
        mainView.replaceViewToUnRecording();
        Intent intent = new Intent(this, FileUploadService.class);
        startService(intent);
    }

    private void startRecording() {

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

    private List<String> getFileNameListFrom(Iterator<DataSnapshot> dataSnapshotIterator) {
        List<String> fileNameList = new ArrayList();
        if (dataSnapshotIterator != null) {
            while (dataSnapshotIterator.hasNext()) {
                fileNameList.add(dataSnapshotIterator.next().getKey());
            }
        }
        return fileNameList;
    }

}
