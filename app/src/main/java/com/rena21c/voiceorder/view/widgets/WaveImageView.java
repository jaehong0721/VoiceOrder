package com.rena21c.voiceorder.view.widgets;


import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.animation.Interpolator;

import com.rena21c.voiceorder.R;

public class WaveImageView extends android.support.v7.widget.AppCompatImageView {

    private final static int OFFSET_Y = -100;

    private final Bitmap image;

    private final Paint p1;
    private final ValueAnimator[] anims;

    private final Interpolator interpolator = new Interpolator() {
        public float getInterpolation(float elapsedTimeRate) {
            int pow = 2;
            if ((elapsedTimeRate *= 2) < 1) {
                return (float) (0.5 * Math.pow(elapsedTimeRate, pow));
            }

            return (float) (1 - 0.5 * Math.abs(Math.pow(2 - elapsedTimeRate, pow)));
        }
    };

    @Override protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(image, 0, OFFSET_Y, null);
        for (ValueAnimator anim : anims) {
            float wave1Xpos = (float) anim.getAnimatedValue("wave1Xpos");
            float wave1Ypos = (float) anim.getAnimatedValue("wave1YPos");
            int alpha = (int) anim.getAnimatedValue("wave1Alpha");
            p1.setAlpha(alpha);
            canvas.drawBitmap(image, wave1Xpos, wave1Ypos + OFFSET_Y, p1);
        }
        super.onDraw(canvas);
        invalidate();
    }

    // 원본 비율: 4300, 원래 비율: 3233
    public WaveImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Resources r = context.getResources();

        image = BitmapFactory.decodeResource(r, R.drawable.wave);

        p1 = new Paint();

        anims = new ValueAnimator[3];
        anims[0] = getValueAnimator(-225, -1600, 90, 115, 0.8f, 0.5f, 3000);
        anims[1] = getValueAnimator(-75, -1525, 97, 105, 0.0f, 0.6f, 2600);
        anims[2] = getValueAnimator(-42, -1650, 90, 132, 0.7f, 0.3f, 2300);

        for (ValueAnimator anim : anims) {
            anim.start();
        }
    }

    private ValueAnimator getValueAnimator(int xPosStart, int xPostEnd, int yPosStart, int yPosEnd, float alphaStart, float alphaEnd, int duration) {
        PropertyValuesHolder wave1XPos = PropertyValuesHolder.ofFloat("wave1Xpos", xPosStart, xPostEnd);
        PropertyValuesHolder wave1YPos = PropertyValuesHolder.ofFloat("wave1YPos", yPosStart, yPosEnd);
        PropertyValuesHolder wave1Alpha = PropertyValuesHolder.ofInt("wave1Alpha", (int) (255 * alphaStart), (int) (255 * alphaEnd));
        ValueAnimator anim = ValueAnimator.ofPropertyValuesHolder(wave1XPos, wave1YPos, wave1Alpha);
        /**
         * Interpolation 참고: https://github.com/MasayukiSuda/EasingInterpolator/blob/master/ei/src/main/java/com/daasuu/ei/EasingProvider.java
         */
        anim.setInterpolator(interpolator);

        anim.setDuration(duration);
        anim.setRepeatCount(ValueAnimator.INFINITE);
        anim.setRepeatMode(ValueAnimator.REVERSE);
        return anim;
    }


}
