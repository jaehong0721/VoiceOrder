package com.rena21c.voiceorder.services;


import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.FirebaseDatabase;
import com.rena21c.voiceorder.R;
import com.rena21c.voiceorder.etc.AppPreferenceManager;
import com.rena21c.voiceorder.firebase.FirebaseDbManager;
import com.rena21c.voiceorder.network.FileTransferUtil;
import com.rena21c.voiceorder.network.NetworkUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * setIntentRedelivery(true):
 * Service의 intent redelivery와 같음.예기치 못한 종료시 인텐트를 재전달함
 */
public class FileUploadService extends IntentService {

    private FirebaseJobDispatcher dispatcher;
    private AwsS3FileUploader fileUploader;
    private FirebaseDbManager dbManager;
    private AppPreferenceManager appPreferenceManager;

    public FileUploadService() {
        super("FileUploadService");
        setIntentRedelivery(true);
    }

    @Override public void onCreate() {
        super.onCreate();
        String bucketName = getResources().getString(R.string.s3_bucket_name);
        fileUploader = new AwsS3FileUploader.Builder()
                .setBucketName(bucketName)
                .setTransferUtility(FileTransferUtil.getTransferUtility(this))
                .build();
        dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(getApplicationContext()));
        dbManager = new FirebaseDbManager(FirebaseDatabase.getInstance());
        appPreferenceManager = new AppPreferenceManager(this);
    }

    @Override protected void onHandleIntent(@Nullable Intent intent) {

        if (NetworkUtil.getConnection(getApplicationContext()) == NetworkUtil.InternetConnection.NOT_CONNECTED) {
            log("인터넷에 연결되지 않았으므로, 서비스 재시작을 방지하고, Job을 등록함");
            setIntentRedelivery(false);
            scheduleJob();
        } else {
            log("파일 업로드 서비스 시작");
            startUploadFile();
        }
    }

    private void uploadNext() {
        if (getRecordFiles().isEmpty()) {
            log("업로드 할 파일이 없음. Service 재시작과 Job을 모두 해제");
            setIntentRedelivery(false);
            dispatcher.cancel(NetworkJobService.JOB_TAG);
        } else { // 모두 성공적으로 끝난 경우
            final File file = getRecordFiles().get(0);
            log("파일 업로드 시작: " + file.getName() + ", size: " + file.length());

            fileUploader.upload(file, new TransferListener() {
                @Override public void onStateChanged(int id, TransferState state) {

                }

                @Override public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) { }

                @Override public void onError(int id, Exception ex) {
                    log("파일 업로드 실패, 모든 작업을 중지하고 Job에 등록함, 예외: " + ex.toString());
                    scheduleJob();
                    setIntentRedelivery(false);
                }
            });
        }
    }

    private void startUploadFile() {
        while (!getRecordFiles().isEmpty()) {
            File file = getRecordFiles().get(0);
            boolean success = uploadFile(file);
            if (!success) {
                scheduleJob();
                setIntentRedelivery(false);
                return; // 오류가 발생한 경우는 잡에 등록한 후 종료함
            }
        }

        setIntentRedelivery(false);
        dispatcher.cancel(NetworkJobService.JOB_TAG);
        Log.d("service", "모든 파일 업로드 완료, 서비스 재시작 방지, Job 해제");
    }


    private boolean uploadFile(final File file) {
        final AtomicBoolean success = new AtomicBoolean(true);
        final CountDownLatch latch = new CountDownLatch(1);
        Log.d("service", "파일 전송 시작: " + file.getName());
        fileUploader.upload(file, new TransferListener() {
            @Override public void onStateChanged(int id, TransferState state) {
                if (state == TransferState.COMPLETED) {

                    addFileNameToDatabase(file);

                    if (file.length() == 0 || !file.exists()) {
                        log("파일 크기 0, 오류 의심 됨: " + file.getName());
                        FirebaseCrash.logcat(Log.ERROR, "service", "파일 업로드 오류 크기 0");
                    }
                    Boolean deleted = file.delete();
                    log("파일 업로드 완료: " + file.getName() + ", 삭제 여부: " + deleted);
                    success.set(true);
                    latch.countDown();
                }
            }

            @Override public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) { }

            @Override public void onError(int id, Exception ex) {
                // 실패한 경우는 redeliver를 취소하고 job에 등록한다.
                log("파일 전송 오류: " + ex.toString());
                success.set(false);
                latch.countDown();
            }
        });
        try {
            latch.await();
            log("Latch 해제");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return success.get();
    }

    private void addFileNameToDatabase(File file) {
        String fileName = file.getName().split("\\.")[0];
        dbManager.addFileName(appPreferenceManager.getPhoneNumber(), fileName, new OnCompleteListener<Void>() {
            @Override public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()) {
                    FirebaseCrash.report(task.getException());
                }
            }
        });
    }

    private List<File> getRecordFiles() {
        List<File> files = new ArrayList<>();
        for (File file : getFilesDir().listFiles()) {
            if (file.getName().endsWith(".mp4")) {
                files.add(file);
            }
        }
        return files;
    }

    private void scheduleJob() {
        log("Job started");
        Job myJob = dispatcher.newJobBuilder()
                .setService(NetworkJobService.class) // the JobService that will be called
                .setTag("file-upload-service")        // uniquely identifies the job
                .setRecurring(true)
                .setLifetime(Lifetime.FOREVER)
                .setTrigger(Trigger.executionWindow(0, 60)) // 첫 실행 이후에는 latest 값으로 실행 됨
                .setReplaceCurrent(true) // 현재 수행중인 작업을 대체함
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .build();
        dispatcher.mustSchedule(myJob);
    }

    private void log(String message) {
        Log.d("service", message);
    }

}
