package com.rena21c.voiceorder.services;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import com.google.android.youtube.player.YouTubeStandalonePlayer;

import java.util.List;

public class YoutubePlayer {
    private String developerKey;
    private String videoId;

    public YoutubePlayer(String developerKey, String videoId) {
        this.developerKey = developerKey;
        this.videoId = videoId;
    }

    public Intent getYoutubeIntent(Activity activity) {
        return YouTubeStandalonePlayer.createVideoIntent(activity, developerKey, videoId, 0, false, false);
    }

    public boolean canPlayVideo(PackageManager packageManager, Intent intent) {
        List<ResolveInfo> resolveInfo = packageManager.queryIntentActivities(intent, 0);
        return resolveInfo != null && !resolveInfo.isEmpty();
    }
}
