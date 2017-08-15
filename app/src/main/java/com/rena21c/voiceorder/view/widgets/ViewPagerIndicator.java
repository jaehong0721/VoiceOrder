package com.rena21c.voiceorder.view.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.rena21c.voiceorder.R;
import com.rena21c.voiceorder.util.DpToPxConverter;

import java.util.ArrayList;

public class ViewPagerIndicator extends RelativeLayout {

    private Context context;
    private ArrayList<View> dots;

    private final int width = DpToPxConverter.convertDpToPx(6,getResources().getDisplayMetrics());
    private final int height = DpToPxConverter.convertDpToPx(6,getResources().getDisplayMetrics());
    private Integer selected;
    private Integer unselected;

    public ViewPagerIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.dots = new ArrayList<>();

        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ViewPagerIndicator);

        selected = typedArray.getResourceId(R.styleable.ViewPagerIndicator_selectedDrawable, -1);
        unselected = typedArray.getResourceId(R.styleable.ViewPagerIndicator_unselectedDrawable, -1);
        typedArray.recycle();
    }

    public void changeDot(int count) {

        if(count < dots.size()) {
            removeView(findViewById(dots.size()));
            dots.remove(dots.size()-1);
            return;
        }

        for (int i = dots.size(); i < count; i++) {

            View dot = new View(context);

            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, height);
            if (dots.size() == 0) {
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            } else {
                layoutParams.leftMargin = 20;
                layoutParams.addRule(RelativeLayout.RIGHT_OF, dots.get(i - 1).getId());
            }
            dot.setLayoutParams(layoutParams);
            dot.setBackgroundResource(R.drawable.unselected_indicator_shape);

            dot.setId(i + 1);
            dots.add(dot);
            addView(dot);
        }
    }

    public void selectDot(int position) {
        if(selected == null || unselected == null) throw new RuntimeException("you must set indicator resource");
        for (View dot : dots) {
            if (dots.get(position) == dot) {
                dot.setBackgroundResource(selected);
            } else {
                dot.setBackgroundResource(unselected);
            }
        }
    }

}
