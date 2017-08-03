package com.rena21c.voiceorder.activities;


import android.os.Bundle;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.rena21c.voiceorder.R;

public class TutorialVideoPlayActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    private String apiKey;
    private String videoId;

    @Override protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_tutorial_video_play);

        YouTubePlayerView videoPlayerView = (YouTubePlayerView) findViewById(R.id.videoPlayerView);

        apiKey = getResources().getString(R.string.google_api_key);
        videoId = getResources().getString(R.string.tutorial_video_id);

        videoPlayerView.initialize(apiKey, this);
    }

    @Override public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        if(youTubePlayer != null) {
            youTubePlayer.cueVideo(videoId);
        } else {
            Toast.makeText(this, "죄송합니다 동영상 재생에 실패하였습니다", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        Toast.makeText(this, "죄송합니다 동영상 재생에 실패하였습니다", Toast.LENGTH_SHORT).show();
        finish();
    }
}
