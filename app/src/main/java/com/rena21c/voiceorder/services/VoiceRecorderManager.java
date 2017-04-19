package com.rena21c.voiceorder.services;


import android.media.MediaRecorder;

import java.io.IOException;

public class VoiceRecorderManager {

    private String fileName;

    public interface VoiceRecordCallback {
        void onStartRecord();
    }

    private final VoiceRecordCallback callback;
    private final String path;

    public VoiceRecorderManager(String path, VoiceRecordCallback callback) {
        this.callback = callback;
        this.path = path;
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
        return fileName;
    }

    private void init() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        recorder.setAudioSamplingRate(44100);
        recorder.setAudioEncodingBitRate(128000);
        recorder.setOutputFile(path + "/" + fileName + ".mp4");
    }

}
