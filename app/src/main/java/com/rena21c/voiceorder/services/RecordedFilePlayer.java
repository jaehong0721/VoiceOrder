package com.rena21c.voiceorder.services;


import android.media.AudioManager;
import android.media.MediaPlayer;

import java.io.IOException;

public class RecordedFilePlayer implements MediaPlayer.OnCompletionListener {

    public interface PlayRecordedFileListener {
        void onPlayRecordedFile(String fileName);
        void onStopRecordedFile();
    }

    private MediaPlayer mediaPlayer;
    private boolean isPlaying;

    private AudioManager audioManager;

    private int currentVolume;

    public RecordedFilePlayer(AudioManager audioManager) {
        this.audioManager = audioManager;
    }

    @Override public void onCompletion(MediaPlayer mp) {
        stopRecordedFile();
    }

    public void playRecordedFile(String path) throws IOException {
        if(isPlaying) return;
        isPlaying = true;

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setDataSource(path);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.prepare();

        int volume = setAudioVolume();
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, AudioManager.FLAG_PLAY_SOUND);

        mediaPlayer.start();
    }

    public void stopRecordedFile() {
        if(!isPlaying) return;
        isPlaying = false;

        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, AudioManager.FLAG_PLAY_SOUND);

        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
    }

    private int setAudioVolume() {
        currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int volume;

        if(currentVolume < maxVolume/2) {
            volume = maxVolume/2;
        } else {
            volume = currentVolume;
        }

        return volume;
    }
}
