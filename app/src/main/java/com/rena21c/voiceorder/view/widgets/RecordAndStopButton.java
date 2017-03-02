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

    private final float BACKGROUND_SCALE_RECORD_X = 1.0F;
    private final float BACKGROUND_SCALE_RECORD_Y = 1.0F;
    private final float BACKGROUND_SCALE_STOP_X = 1.1F;
    private final float BACKGROUND_SCALE_STOP_Y = 1.1F;

    private final float FROM_ALPHA = 0.5F;
    private final float TO_ALPHA = 0;

    private final float PULSE_SCALE_RECORD_X = 1.3F;
    private final float PULSE_SCALE_RECORD_Y = 1.3F;
    private final float PULSE_SCALE_STOP_X = 1.4F;
    private final float PULSE_SCALE_STOP_Y = 1.4F;

    private final int PULSE_CYCLE = 1000;

    private boolean isRecord = false;

    private RelativeLayout buttonLayout;
    private ImageView ivAnimation;
    private ImageView ivPlayAndStop;
    private ImageView ivBackground;
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
        init();
        startPulseAnimationRecordBtn();
    }

    private void init() {
        ivPlayAndStop = (ImageView)findViewById(R.id.ivPlayAndStop);
        ivPlayAndStop.setOnClickListener(this);

        ivBackground = (ImageView)findViewById(R.id.ivBackground);
        ivAnimation = (ImageView)findViewById(R.id.ivAnimation);
        buttonLayout = (RelativeLayout) findViewById(R.id.buttonLayout);
        ivStop = (ImageView)findViewById(R.id.ivStop);
        ivStop.setOnClickListener(this);
    }

    public void startPulseAnimationRecordBtn() {
        ivAnimation.clearAnimation();

        AnimationSet set = new AnimationSet(true);
        set.setDuration(PULSE_CYCLE);
        set.setInterpolator(new LinearInterpolator());

        AlphaAnimation alphaAnimation = new AlphaAnimation(FROM_ALPHA, TO_ALPHA);
        alphaAnimation.setRepeatCount(-1);
        set.addAnimation(alphaAnimation);

        ScaleAnimation scaleAnimation = new ScaleAnimation(BACKGROUND_SCALE_RECORD_X, PULSE_SCALE_RECORD_X,
                                                            BACKGROUND_SCALE_RECORD_Y, PULSE_SCALE_RECORD_Y,
                                                            Animation.RELATIVE_TO_SELF, 0.5F,
                                                            Animation.RELATIVE_TO_SELF, 0.5F);
        scaleAnimation.setRepeatCount(-1);
        set.addAnimation(scaleAnimation);

        ivAnimation.startAnimation(set);

    }

    public void startPulseAnimationStopBtn() {
        ivAnimation.clearAnimation();

        AnimationSet set = new AnimationSet(true);
        set.setDuration(PULSE_CYCLE);
        set.setInterpolator(new LinearInterpolator());

        AlphaAnimation alphaAnimation = new AlphaAnimation(0.5F, 0);
        alphaAnimation.setRepeatCount(-1);
        set.addAnimation(alphaAnimation);

        ScaleAnimation scaleAnimation = new ScaleAnimation(BACKGROUND_SCALE_STOP_X, PULSE_SCALE_STOP_X,
                                                            BACKGROUND_SCALE_STOP_Y, PULSE_SCALE_STOP_Y,
                                                            Animation.RELATIVE_TO_SELF, 0.5F,
                                                            Animation.RELATIVE_TO_SELF, 0.5F);
        scaleAnimation.setRepeatCount(-1);
        set.addAnimation(scaleAnimation);


        ivAnimation.startAnimation(set);
    }

    @Override
    public void onClick(View v) {

        if(isRecord) {
            isRecord = false;
            //listener.btn_stop();
            setRecordButton();

        }
        else {
            isRecord = true;
            //listener.record();
            setStopButton();
        }
    }

    public void setRecordButton() {
        ivBackground.animate().scaleX(BACKGROUND_SCALE_RECORD_X).scaleY(BACKGROUND_SCALE_RECORD_Y).start();
        buttonLayout.animate().translationYBy(100).setDuration(100).start();
        startPulseAnimationRecordBtn();
        ivPlayAndStop.setVisibility(View.VISIBLE);
        ivStop.setVisibility(View.INVISIBLE);
    }

    public void setStopButton() {
        ivBackground.animate().scaleX(BACKGROUND_SCALE_STOP_X).scaleY(BACKGROUND_SCALE_STOP_Y).start();
        buttonLayout.animate().translationYBy(-100).setDuration(100).start();
        startPulseAnimationStopBtn();
        ivPlayAndStop.setVisibility(View.INVISIBLE);
        ivStop.setVisibility(View.VISIBLE);
    }

}
