package com.rena21c.voiceorder.view.animation;


import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class ShowHeightChangeAnimation extends Animation {

    int startHeight;
    int targetHeight;
    View view;

    public ShowHeightChangeAnimation(View view, int targetHeight) {
        this.view = view;
        this.startHeight = view.getHeight();
        this.targetHeight = targetHeight;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {

        int newHeight = (int) (startHeight + (targetHeight - startHeight) * interpolatedTime);
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
