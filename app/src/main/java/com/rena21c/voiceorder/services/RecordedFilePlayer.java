package com.rena21c.voiceorder.services;


import android.media.MediaPlayer;

import java.io.IOException;

public class RecordedFilePlayer implements MediaPlayer.OnCompletionListener {

    @Override public void onCompletion(MediaPlayer mp) {
        stopRecordedFile();
    }

    public interface PlayRecordedFileListener {
        void onPlayRecordedFile(String fileName);
        void onStopRecordedFile();
    }

    private MediaPlayer mediaPlayer;
    private boolean isPlaying;

    public void playRecordedFile(String path) throws IOException {
        if(isPlaying) return;
        isPlaying = true;

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setDataSource(path);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.prepare();
        mediaPlayer.start();
    }

    public void stopRecordedFile() {
        if(!isPlaying) return;
        isPlaying = false;

        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
    }
}
