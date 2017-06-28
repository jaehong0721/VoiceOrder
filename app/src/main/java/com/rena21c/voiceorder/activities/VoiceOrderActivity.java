package com.rena21c.voiceorder.activities;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.rena21c.voiceorder.App;
import com.rena21c.voiceorder.R;
import com.rena21c.voiceorder.etc.AppPreferenceManager;
import com.rena21c.voiceorder.etc.RecordedFileManager;
import com.rena21c.voiceorder.firebase.FirebaseDbManager;
import com.rena21c.voiceorder.firebase.ToastErrorHandlingListener;
import com.rena21c.voiceorder.network.NetworkUtil;
import com.rena21c.voiceorder.services.FileUploadService;
import com.rena21c.voiceorder.services.RecordedFilePlayer;
import com.rena21c.voiceorder.services.VoiceRecorderManager;
import com.rena21c.voiceorder.util.FileNameUtil;
import com.rena21c.voiceorder.util.MemorySizeChecker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class VoiceOrderActivity extends HasTabActivity implements VoiceRecorderManager.VoiceRecordCallback,
                                                                    RecordedFilePlayer.PlayRecordedFileListener {

    private final long REQUIRED_SPACE = 5L * 1024L * 1024L;

    private AppPreferenceManager appPreferenceManager;
    private VoiceRecorderManager recordManager;
    private FirebaseDbManager dbManager;
    private MemorySizeChecker memorySizeChecker;
    private RecordedFileManager recordedFileManager;
    private RecordedFilePlayer recordedFilePlayer;

    private ChildEventListener acceptedOrderChildEventListener;

    private VoiceOrderView voiceOrderView;

    private Query acceptedOrderQuery;
    private BroadcastReceiver fileUploadSuccessReceiver;
    private ChildEventListener recordListListener;
    private Query recordListQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("VoiceOrderActivity", "OnCreate");
        setTheme(R.style.MainTheme);
        setContentView(R.layout.activity_voice_order);

        fileUploadSuccessReceiver = new BroadcastReceiver() {
            @Override public void onReceive(Context context, Intent intent) {
                String file = intent.getStringExtra("file");
                boolean success = intent.getBooleanExtra("success", false);
                Log.d("VoiceOrderActivity", "파일 전송 완료, 파일: " + file + ", 성공: " + success);
            }
        };

        appPreferenceManager = App.getApplication(getApplicationContext()).getPreferenceManager();
        recordedFileManager = App.getApplication(getApplicationContext()).getRecordedFileManager();
        dbManager = new FirebaseDbManager(FirebaseDatabase.getInstance());
        recordedFilePlayer = new RecordedFilePlayer();

        voiceOrderView = new VoiceOrderView(VoiceOrderActivity.this);
        voiceOrderView.initView(dbManager, recordedFileManager);

        recordManager = new VoiceRecorderManager(recordedFileManager, this);
        memorySizeChecker = new MemorySizeChecker(REQUIRED_SPACE);

        acceptedOrderChildEventListener = new ChildEventListener() {
            @Override public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d("DB", "added: " + dataSnapshot.toString());
                voiceOrderView.addOrder(dataSnapshot);
            }

            @Override public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d("DB", "changed: " + dataSnapshot.toString());
                voiceOrderView.replaceAcceptedOrder(dataSnapshot);
            }

            @Override public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d("DB", "removed: " + dataSnapshot.toString());
                dbManager.removeRecordedOrder(appPreferenceManager.getPhoneNumber(), dataSnapshot.getKey());
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
                    voiceOrderView.addTimeStamp(fileName);
                }
            }
        });

        recordListListener = new ChildEventListener() {
            @Override public void onChildAdded(DataSnapshot dataSnapshot, String s) { }

            @Override public void onChildChanged(DataSnapshot dataSnapshot, String s) { }

            @Override public void onChildRemoved(DataSnapshot dataSnapshot) {
                String timeStamp = FileNameUtil.getTimeFromFileName(dataSnapshot.getKey());
                voiceOrderView.remove(timeStamp);
            }

            @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) { }

            @Override public void onCancelled(DatabaseError databaseError) { }
        };

        recordListQuery = dbManager.subscribeRecordedOrder(appPreferenceManager.getPhoneNumber(), recordListListener);

    }

    @Override protected void onStart() {
        super.onStart();
        registerReceiver(fileUploadSuccessReceiver, new IntentFilter("com.rena21c.voiceorder.ACTION_UPLOAD"));
        voiceOrderView.setView(appPreferenceManager.getUserFirstVisit());
    }

    @Override protected void onPause() {
        super.onPause();
        recordedFilePlayer.stopRecordedFile();
    }

    @Override
    protected void onStop() {
        super.onStop();
        recordManager.cancel();
        voiceOrderView.clearKeepScreenOn();
        if(!appPreferenceManager.getUserFirstVisit()) voiceOrderView.replaceViewToUnRecording();
        unregisterReceiver(fileUploadSuccessReceiver);
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        acceptedOrderQuery.removeEventListener(acceptedOrderChildEventListener);
        recordListQuery.removeEventListener(recordListListener);
    }

    @Override public void onStartRecord() {
        recordedFilePlayer.stopRecordedFile();
        voiceOrderView.replaceViewToRecording();
    }

    @Override public void onPlayRecordedFile(String fileName) {
        try {
            String path = recordedFileManager.getRecordedFilePath(fileName);
            recordedFilePlayer.playRecordedFile(path);
        } catch (IOException e) {
            Toast.makeText(this, "녹음파일 재생에 실패하였습니다\n잠시후 다시 시도해주세요", Toast.LENGTH_SHORT).show();
        }
    }

    @Override public void onStopRecordedFile() {
        recordedFilePlayer.stopRecordedFile();
    }

    public void onStartedRecording() {
        if (memorySizeChecker.isEnough()) {
            voiceOrderView.showDialog(VoiceOrderView.NO_INTERNAL_MEMORY);
        } else {
            voiceOrderView.setKeepScreenOn();
            startRecording();
        }
    }

    public void onStoppedRecording() {
        final String fileName = recordManager.stop();
        voiceOrderView.clearKeepScreenOn();
        voiceOrderView.addEmptyOrderToViewPager(fileName);
        voiceOrderView.replaceViewToUnRecording();
        Intent intent = new Intent(this, FileUploadService.class);
        startService(intent);
    }

    private void startRecording() {

        if (!NetworkUtil.isInternetConnected(getApplicationContext())) {
            voiceOrderView.showDialog(VoiceOrderView.NO_INTERNET_CONNECT);
        } else {
            if (appPreferenceManager.getUserFirstVisit()) {
                appPreferenceManager.setUserFirstVisit();
            }
            String fileName = FileNameUtil.makeFileName(appPreferenceManager.getPhoneNumber(), System.currentTimeMillis());
            recordManager.start(fileName);
        }

    }

    private List<String> getFileNameListFrom(Iterator<DataSnapshot> dataSnapshotIterator) {
        List<String> fileNameList = new ArrayList<>();
        if (dataSnapshotIterator != null) {
            while (dataSnapshotIterator.hasNext()) {
                fileNameList.add(dataSnapshotIterator.next().getKey());
            }
        }
        return fileNameList;
    }
}
