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
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.rena21c.voiceorder.App;
import com.rena21c.voiceorder.R;
import com.rena21c.voiceorder.etc.AppPreferenceManager;
import com.rena21c.voiceorder.firebase.FirebaseDbManager;
import com.rena21c.voiceorder.firebase.ToastErrorHandlingListener;
import com.rena21c.voiceorder.model.Order;
import com.rena21c.voiceorder.model.VendorInfo;
import com.rena21c.voiceorder.model.VoiceRecord;
import com.rena21c.voiceorder.network.FileTransferUtil;
import com.rena21c.voiceorder.network.NetworkUtil;
import com.rena21c.voiceorder.services.AwsS3FileUploader;
import com.rena21c.voiceorder.services.VoiceRecorderManager;
import com.rena21c.voiceorder.util.Container;
import com.rena21c.voiceorder.util.FileNameUtil;
import com.rena21c.voiceorder.util.MemorySizeChecker;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class MainActivity extends BaseActivity implements VoiceRecorderManager.VoiceRecordCallback {

    private final long REQUIRED_SPACE = 5L * 1024L * 1024L;

    private AppPreferenceManager appPreferenceManager;
    private VoiceRecorderManager recordManager;
    private FirebaseDbManager dbManager;
    private MemorySizeChecker memorySizeChecker;
    private AwsS3FileUploader fileUploader;
    private ChildEventListener acceptedOrderChildEventListener;

    private MainView mainView;

    private boolean isUploading;
    private Query acceptedOrderQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("MainActivity", "OnCreate");
        setContentView(R.layout.activity_main);


        new Thread(new Runnable() {
            @Override public void run() {
                dataLoadSync();
            }
        }).start();

        appPreferenceManager = App.getApplication(getApplicationContext()).getPreferenceManager();

        recordManager = new VoiceRecorderManager(getFilesDir().getPath(), this);

        dbManager = new FirebaseDbManager(FirebaseDatabase.getInstance());

        memorySizeChecker = new MemorySizeChecker(REQUIRED_SPACE);

        mainView = new MainView(MainActivity.this);

        fileUploader = new AwsS3FileUploader.Builder()
                .setBucketName(getResources().getString(R.string.s3_bucket_name))
                .setTransferUtility(FileTransferUtil.getTransferUtility(this))
                .build();

        acceptedOrderChildEventListener = new ChildEventListener() {
            @Override public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d("DB", "added: " + dataSnapshot.toString());
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
    }


    private HashMap<String, HashMap<String, VoiceRecord>> acceptedOrderMap;

    private void dataLoadSync() {
        Log.e("lifeCycle", "dataLoadSync");
        final CountDownLatch latch = new CountDownLatch(2);

        final Container<List<String>> fileNameListContainter = new Container<>();

        dbManager.getRecordOrder(appPreferenceManager.getPhoneNumber(), new ToastErrorHandlingListener(this) {
            @Override public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator recordFileMapType = new GenericTypeIndicator<HashMap<String, HashMap<String, String>>>() {};
                fileNameListContainter.setObject(getSortedListFromMap((HashMap) dataSnapshot.getValue(recordFileMapType)));
                latch.countDown();
            }
        });

        //오퍼레이터 접수 후 데이터 로드
        dbManager.getAcceptedOrder(appPreferenceManager.getPhoneNumber(), new ToastErrorHandlingListener(this) {
            @Override public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator objectMapType = new GenericTypeIndicator<HashMap<String, HashMap<String, VoiceRecord>>>() {};
                acceptedOrderMap = (HashMap) dataSnapshot.getValue(objectMapType);
                latch.countDown();
            }
        });

        try {
            latch.await();
            // TODO: 앱 재시작시 App 객체의 order가 삭제 되지 않으므로, 초기화를 수행함
            final ArrayList<Order> orders = getOrders(fileNameListContainter.getObject());
            runOnUiThread(new Runnable() {
                @Override public void run() {
                    initView(orders);
                }
            });
        } catch (InterruptedException e) {}
    }

    private void initView(ArrayList<Order> orders) {
        mainView.initView(appPreferenceManager.getUserFirstVisit(), dbManager, orders);
    }

    @Override protected void onStart() {
        super.onStart();
        acceptedOrderQuery = dbManager.subscribeAcceptedOrder(appPreferenceManager.getPhoneNumber(), acceptedOrderChildEventListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        acceptedOrderQuery.removeEventListener(acceptedOrderChildEventListener);
        recordManager.stop();
        mainView.clearKeepScreenOn();
        mainView.replaceViewToUnRecording();
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
        mainView.addOrderToViewPager(FileNameUtil.getTimeFromFileName(fileName));
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

    private void storeFileName(final String fileName) {
        dbManager.addFileName(appPreferenceManager.getPhoneNumber(), fileName, new OnCompleteListener<Void>() {
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

    private List<String> getSortedListFromMap(HashMap<String, HashMap<String, String>> recordedFileMap) {
        List<String> fileNameList = new ArrayList();
        if (recordedFileMap != null) {
            for (HashMap<String, String> fileNameMap : recordedFileMap.values()) {
                fileNameList.add(fileNameMap.get("fileName"));
            }
            Collections.sort(fileNameList, Collections.<String>reverseOrder());
        }
        return fileNameList;
    }


    private ArrayList<Order> getOrders(List<String> fileNameList) {
        ArrayList<Order> result = new ArrayList<>();

        if (fileNameList.isEmpty()) return result;

        if (acceptedOrderMap == null) acceptedOrderMap = new HashMap<>();

        for (String fileName : fileNameList) {
            String timeStamp = FileNameUtil.getTimeFromFileName(fileName);
            if (acceptedOrderMap.containsKey(fileName)) {
                HashMap<String, VoiceRecord> itemHashMap = getVendorName(acceptedOrderMap.get(fileName));
                result.add(new Order(Order.OrderState.ACCEPTED, timeStamp, itemHashMap));
            } else {
                result.add(new Order(Order.OrderState.IN_PROGRESS, timeStamp, null));
            }
        }

        return result;
    }

    private HashMap getVendorName(final HashMap<String, VoiceRecord> itemHashMap) {
        for (final String vendorPhoneNumber : itemHashMap.keySet()) {
            dbManager.getVendorInfo(vendorPhoneNumber, new ToastErrorHandlingListener(this) {
                @Override public void onDataChange(DataSnapshot dataSnapshot) {
                    VendorInfo vendorInfo = dataSnapshot.getValue(VendorInfo.class);
                    VoiceRecord toRemove = itemHashMap.remove(vendorPhoneNumber);
                    if (toRemove != null) {
                        itemHashMap.put(vendorInfo.vendorName, toRemove);
                    }
                }
            });
        }
        return itemHashMap;
    }
    
}
