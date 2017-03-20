package com.rena21c.voiceorder.view.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.RelativeLayout;

import com.rena21c.voiceorder.R;

import java.util.ArrayList;

public class ViewPagerIndicator extends RelativeLayout {

    private Context context;
    private ArrayList<View> dots;

    private final int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6, getResources().getDisplayMetrics());
    private final int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6, getResources().getDisplayMetrics());

    public ViewPagerIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.dots = new ArrayList<>();
    }

    public void createDot(int count) {
        Log.e("indicator", dots.size() + "");
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
        for(View dot : dots) {
            if(dots.get(position) == dot) {
                dot.setBackgroundResource(R.drawable.selected_indicator_shape);
            }
            else {
                dot.setBackgroundResource(R.drawable.unselected_indicator_shape);
            }
        }
    }

    public void addDot() {
        createDot(dots.size()+1);
    }

}
