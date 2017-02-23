package com.rena21c.voiceorder.widget;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.rena21c.voiceorder.R;


public class SplashDialog extends DialogFragment {

    ImageView ivSplashInDialog;
    AnimationDrawable frameAnimation;

    public static SplashDialog newInstance() {

        SplashDialog splashDialog = new SplashDialog();

        return splashDialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme_DeviceDefault_NoActionBar_Fullscreen);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.widget_splash_dialog, null);

        ivSplashInDialog = (ImageView) view.findViewById(R.id.ivSplashInDialog);
        ivSplashInDialog.setBackgroundResource(R.drawable.intro_ani);
        frameAnimation = (AnimationDrawable) ivSplashInDialog.getBackground();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        setAnimation();
        startAnimation();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        frameAnimation.stop();
    }

    public void setAnimation() {
        frameAnimation.setEnterFadeDuration(500);
        frameAnimation.setExitFadeDuration(500);
        frameAnimation.setOneShot(false);
    }
    public void startAnimation() {
        frameAnimation.start();
    }
}
