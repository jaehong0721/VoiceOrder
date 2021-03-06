package com.rena21c.voiceorder.activities;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.rena21c.voiceorder.App;
import com.rena21c.voiceorder.R;
import com.rena21c.voiceorder.etc.AppPreferenceManager;
import com.rena21c.voiceorder.etc.RecordedFileManager;
import com.rena21c.voiceorder.firebase.AnalyticsEventManager;
import com.rena21c.voiceorder.firebase.FirebaseDbManager;
import com.rena21c.voiceorder.network.NetworkUtil;
import com.rena21c.voiceorder.services.FileUploadService;
import com.rena21c.voiceorder.services.RecordedFilePlayer;
import com.rena21c.voiceorder.services.VoiceRecorderManager;
import com.rena21c.voiceorder.util.FileNameUtil;
import com.rena21c.voiceorder.util.MemorySizeChecker;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class VoiceOrderActivity extends HasTabActivity implements VoiceRecorderManager.VoiceRecordCallback,
                                                                    RecordedFilePlayer.PlayRecordedFileListener {


    private final long REQUIRED_SPACE = 5L * 1024L * 1024L;

    private AppPreferenceManager appPreferenceManager;
    private AnalyticsEventManager eventManager;
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

    private String targetVendor;

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
        eventManager = App.getApplication(getApplicationContext()).getEventManager();
        recordedFileManager = App.getApplication(getApplicationContext()).getRecordedFileManager();
        dbManager = App.getApplication(getApplicationContext()).getDbMangaer();
        recordedFilePlayer = new RecordedFilePlayer((AudioManager)getSystemService(Context.AUDIO_SERVICE));

        voiceOrderView = new VoiceOrderView(this,dbManager, recordedFileManager);

        recordManager = new VoiceRecorderManager(recordedFileManager, this);
        memorySizeChecker = new MemorySizeChecker(REQUIRED_SPACE);

        recordListListener = new ChildEventListener() {
            @Override public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String fileName = dataSnapshot.getKey();
                voiceOrderView.addTimeStamp(fileName);
            }

            @Override public void onChildChanged(DataSnapshot dataSnapshot, String s) { }

            @Override public void onChildRemoved(DataSnapshot dataSnapshot) {
                String timeStamp = FileNameUtil.getTimeFromFileName(dataSnapshot.getKey());
                voiceOrderView.removeTimeStamp(timeStamp);
            }

            @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) { }

            @Override public void onCancelled(DatabaseError databaseError) { }
        };

        recordListQuery = dbManager.subscribeRecordedOrder(appPreferenceManager.getPhoneNumber(), recordListListener);

        acceptedOrderChildEventListener = new ChildEventListener() {
            String removedDataSnapshotKey = "";

            @Override public void onChildAdded(final DataSnapshot dataSnapshot, String s) {
                Log.d("DB", "added: " + dataSnapshot.toString());
                //하나의 품목만 들어간 주문을 수정하면, removed-added 로 수정이 되므로 삭제된 파일이름 정보를 다시 저장해줘야함
                if(removedDataSnapshotKey.equals(dataSnapshot.getKey())) {
                    dbManager.addFileName(appPreferenceManager.getPhoneNumber(), dataSnapshot.getKey(), new OnCompleteListener() {
                        @Override public void onComplete(@NonNull Task task) {
                            if (!task.isSuccessful()) {
                                FirebaseCrash.report(task.getException());
                            } else {
                                voiceOrderView.addOrder(appPreferenceManager.getPhoneNumber(),dataSnapshot);
                            }
                        }
                    });
                } else {
                    voiceOrderView.addOrder(appPreferenceManager.getPhoneNumber(),dataSnapshot);
                }
            }

            @Override public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d("DB", "changed: " + dataSnapshot.toString());
                voiceOrderView.replaceAcceptedOrder(appPreferenceManager.getPhoneNumber(),dataSnapshot);
            }

            @Override public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d("DB", "removed: " + dataSnapshot.toString());
                removedDataSnapshotKey = dataSnapshot.getKey();
                dbManager.removeRecordedOrder(appPreferenceManager.getPhoneNumber(), removedDataSnapshotKey);
                voiceOrderView.removeOrder(dataSnapshot);
            }

            @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

            @Override public void onCancelled(DatabaseError databaseError) {}
        };

        acceptedOrderQuery = dbManager.subscribeAcceptedOrder(appPreferenceManager.getPhoneNumber(), acceptedOrderChildEventListener);

        targetVendor = getIntent().getStringExtra("direct");
    }

    @Override protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        targetVendor = intent.getStringExtra("direct");
    }

    @Override protected void onStart() {
        super.onStart();
        registerReceiver(fileUploadSuccessReceiver, new IntentFilter("com.rena21c.voiceorder.ACTION_UPLOAD"));
        voiceOrderView.setView(appPreferenceManager.getUserFirstRecord());

        if(targetVendor == null) return;
        voiceOrderView.callOnClickRecord();
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
        if(!appPreferenceManager.getUserFirstRecord()) voiceOrderView.replaceViewToUnRecording();
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
            eventManager.setRePlayOrderEvent();
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
            eventManager.setVoiceOrderEvent();
            voiceOrderView.setKeepScreenOn();
            startRecording();
        }
    }

    public void onStoppedRecording() {
        String fileName = recordManager.stop();
        if(targetVendor != null) makeTargetVendorTextFile(fileName);
        voiceOrderView.clearKeepScreenOn();
        voiceOrderView.replaceViewToUnRecording();
        Intent intent = new Intent(this, FileUploadService.class);
        startService(intent);
    }


    public void playTutorialVideo() {
        Intent tutorialIntent = new Intent(VoiceOrderActivity.this, TutorialVideoPlayActivity.class);
        startActivity(tutorialIntent);
    }

    private void startRecording() {
        if (!NetworkUtil.isInternetConnected(getApplicationContext())) {
            voiceOrderView.showDialog(VoiceOrderView.NO_INTERNET_CONNECT);
        } else {
            if (appPreferenceManager.getUserFirstRecord()) {
                appPreferenceManager.setUserFirstRecord();
            }
            String fileName = FileNameUtil.makeFileName(appPreferenceManager.getPhoneNumber(), System.currentTimeMillis());
            recordManager.start(fileName);
        }

    }

    private void makeTargetVendorTextFile(String fileName) {
        File textFile = new File(getFilesDir().getPath(), fileName + ".txt");
        FileWriter writer;
        try {
            writer = new FileWriter(textFile,true);
            writer.append(targetVendor);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            textFile.delete();
            e.printStackTrace();
        } finally {
            targetVendor = null;
        }
    }
}
