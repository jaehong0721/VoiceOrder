package com.rena21c.voiceorder.view.widgets;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.rena21c.voiceorder.R;

public class RecordAndStopButton extends FrameLayout implements View.OnClickListener{


    private boolean isRecord = false;
    private RelativeLayout buttonLayout;

    private ImageView ivAnimation;
    private ImageView ivBackground;
    private ImageView ivPlay;
    private ImageView ivStop;

    private activateRecorderListener listener;

    public interface activateRecorderListener {
        void record();
        void stop();
    }

    public void setListener(activateRecorderListener listener) {
        this.listener = listener;
    }

    public RecordAndStopButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        inflate(context, R.layout.widget_record_and_stop_button, this);

        ivPlay = (ImageView)findViewById(R.id.ivPlay);
        ivPlay.setOnClickListener(this);

        ivBackground = (ImageView)findViewById(R.id.ivBackground);
        ivAnimation = (ImageView)findViewById(R.id.ivAnimation);
        buttonLayout = (RelativeLayout) findViewById(R.id.buttonLayout);
        ivStop = (ImageView)findViewById(R.id.ivStop);
        ivStop.setOnClickListener(this);
        startPulseAnimationRecordBtn();
    }

    public void startPulseAnimationRecordBtn() {
        AnimationSet set = new AnimationSet(true);
        set.setDuration(1000);
        set.setInterpolator(new LinearInterpolator());

        AlphaAnimation alphaAnimation = new AlphaAnimation(0.5F, 0);
        alphaAnimation.setRepeatCount(-1);
        set.addAnimation(alphaAnimation);

        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0F,1.5F,1.0F,1.5F, Animation.RELATIVE_TO_SELF, 0.5F, Animation.RELATIVE_TO_SELF, 0.5F);
        scaleAnimation.setRepeatCount(-1);
        set.addAnimation(scaleAnimation);

        ivAnimation.clearAnimation();
        ivAnimation.startAnimation(set);

    }

    public void startPulseAnimationStopBtn() {
        AnimationSet set = new AnimationSet(true);
        set.setDuration(1000);
        set.setInterpolator(new LinearInterpolator());

        AlphaAnimation alphaAnimation = new AlphaAnimation(0.5F, 0);
        alphaAnimation.setRepeatCount(-1);
        set.addAnimation(alphaAnimation);

        ScaleAnimation scaleAnimation = new ScaleAnimation(0.9F,1.4F,0.9F,1.4F, Animation.RELATIVE_TO_SELF, 0.5F, Animation.RELATIVE_TO_SELF, 0.5F);
        scaleAnimation.setRepeatCount(-1);
        set.addAnimation(scaleAnimation);

        ivAnimation.clearAnimation();
        ivAnimation.startAnimation(set);
    }

    @Override
    public void onClick(View v) {

        if(isRecord) {
            isRecord = false;
            //listener.stop();
            setRecordButton();

        }
        else {
            isRecord = true;
            //listener.record();
            setStopButton();
        }
    }

    public void setRecordButton() {
        ivBackground.animate().scaleX(1.0F).scaleY(1.0F).start();
        buttonLayout.animate().translationYBy(100).setDuration(100).start();
        startPulseAnimationRecordBtn();
        ivPlay.setVisibility(View.VISIBLE);
        ivStop.setVisibility(View.INVISIBLE);
    }

    public void setStopButton() {
        ivBackground.animate().scaleX(0.9F).scaleY(0.9F).start();
        buttonLayout.animate().translationYBy(-100).setDuration(100).start();
        startPulseAnimationStopBtn();
        ivPlay.setVisibility(View.INVISIBLE);
        ivStop.setVisibility(View.VISIBLE);
    }
}
