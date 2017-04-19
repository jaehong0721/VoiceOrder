package com.rena21c.voiceorder.services;

import android.content.Intent;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

public class NetworkJobService extends JobService {

    public static String JOB_TAG = "file-upload-service";

    @Override public boolean onStartJob(JobParameters job) {
        Log.d("service", "Job service started");
        Intent intent = new Intent(getApplicationContext(), FileUploadService.class);
        startService(intent);
        return false;
    }

    @Override public boolean onStopJob(JobParameters job) {
        return false;
    }

}
