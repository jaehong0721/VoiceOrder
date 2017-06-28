package com.rena21c.voiceorder.services;


import android.media.MediaRecorder;

import com.rena21c.voiceorder.etc.RecordedFileManager;

import java.io.File;
import java.io.IOException;

public class VoiceRecorderManager {

    private String fileName;

    public interface VoiceRecordCallback {
        void onStartRecord();
    }

    private final VoiceRecordCallback callback;
    private final RecordedFileManager recordedFileManager;

    private final String path;

    public VoiceRecorderManager(RecordedFileManager recordedFileManager, VoiceRecordCallback callback) {
        this.callback = callback;
        this.recordedFileManager = recordedFileManager;
        this.path = recordedFileManager.getRootDir();
    }

    private MediaRecorder recorder;

    public void start(String fileName) {
        this.fileName = fileName;
        init();
        try {
            recorder.prepare();
            recorder.start();
            callback.onStartRecord();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String stop() {
        if (recorder != null) {
            recorder.stop();
            recorder.release();
            recorder = null;
        }

        recordedFileManager.createSaveDir();

        try {
            recordedFileManager.saveRecordedFile(new File(path + "/temp"), fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return fileName;
    }

    public void cancel() {
        if (recorder != null) {
            recorder.stop();
            recorder.release();
            recorder = null;
        }

        new File(path + "/temp").delete();
    }

    private void init() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        recorder.setAudioSamplingRate(44100);
        recorder.setAudioEncodingBitRate(128000);
        recorder.setOutputFile(path + "/temp");
    }
}
