package com.rena21c.voiceorder.view.widgets;


import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.Transformation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.rena21c.voiceorder.R;

public class RecordAndStopButton extends FrameLayout implements View.OnClickListener{

    public final int HEIGHT_WITH_GUIDE_LAYOUT = 222;
    public final int HEIGHT_WITH_ORDER_LIST_LAYOUT = 167;
    private final int HEIGHT_WITH_RECORDING_LAYOUT = 313;

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

    private final int PULSE_CYCLE = 700;

    private boolean isRecording = false;

    private RelativeLayout buttonLayout;
    private ImageView ivAnimation;
    private ImageView ivBackground;
    private ImageView ivRecord;
    private ImageView ivStop;

    private activateRecorderListener listener;

    public interface activateRecorderListener {
        void onStartRecording();
        void onStopRecording();
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
        ivRecord = (ImageView)findViewById(R.id.ivRecord);
        ivStop = (ImageView)findViewById(R.id.ivStop);
      
        ivBackground = (ImageView)findViewById(R.id.ivBackground);
        ivBackground.setOnClickListener(this);
        ivAnimation = (ImageView)findViewById(R.id.ivAnimation);
        buttonLayout = (RelativeLayout) findViewById(R.id.buttonLayout);


    }

    public void setInitHeight(int height) {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)buttonLayout.getLayoutParams();
        params.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, height, getResources().getDisplayMetrics());
        buttonLayout.setLayoutParams(params);
    }

    private void startPulseAnimationRecordBtn() {
        ivAnimation.clearAnimation();

        AnimationSet set = new AnimationSet(true);
        set.setDuration(PULSE_CYCLE);
        set.setStartOffset(400);
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

    private void startPulseAnimationStopBtn() {
        ivAnimation.clearAnimation();

        AnimationSet set = new AnimationSet(true);
        set.setDuration(PULSE_CYCLE);
        set.setStartOffset(500);
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

        if(isRecording) {
            listener.onStopRecording();
        }
        else {
            listener.onStartRecording();
        }
    }

    public void setRecordButton() {
        isRecording = false;
        Animation shortHeightAni = new showHeightChangeAnimation(buttonLayout, HEIGHT_WITH_ORDER_LIST_LAYOUT);
        shortHeightAni.setDuration(200);
        buttonLayout.startAnimation(shortHeightAni);

        ivBackground.animate().setInterpolator(new BounceInterpolator()).scaleX(BACKGROUND_SCALE_RECORD_X).scaleY(BACKGROUND_SCALE_RECORD_Y).start();

        startPulseAnimationRecordBtn();

        ivRecord.setVisibility(View.VISIBLE);
        ivStop.setVisibility(View.INVISIBLE);
    }

    public void setStopButton() {
        isRecording = true;
        Animation longHeightAni = new showHeightChangeAnimation(buttonLayout, HEIGHT_WITH_RECORDING_LAYOUT);
        longHeightAni.setDuration(200);
        buttonLayout.startAnimation(longHeightAni);

        ivBackground.animate().setInterpolator(new BounceInterpolator()).scaleX(BACKGROUND_SCALE_STOP_X).scaleY(BACKGROUND_SCALE_STOP_Y).start();

        startPulseAnimationStopBtn();

        ivRecord.setVisibility(View.INVISIBLE);
        ivStop.setVisibility(View.VISIBLE);
    }

    public class showHeightChangeAnimation extends Animation {
        int startHeight;
        int targetHeight;
        View view;

        public showHeightChangeAnimation(View view, int targetHeight) {
            this.view = view;
            this.startHeight = view.getHeight();
            this.targetHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, targetHeight, getResources().getDisplayMetrics());
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {

            int newHeight = (int) (startHeight+(targetHeight - startHeight) * interpolatedTime);
            view.getLayoutParams().height = newHeight;
            view.requestLayout();
        }

        @Override
        public void initialize(int width, int height, int parentWidth, int parentHeight) {
            super.initialize(width, height, parentWidth, parentHeight);
        }

        @Override
        public boolean willChangeBounds() {
            return true;
        }
    }

    public boolean isRecording() {
        return isRecording;
    }

}
