package com.rena21c.voiceorder.view.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

public class ReplaceableLayout extends RelativeLayout{

    Context context;

    public ReplaceableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public void replaceChildView(View childView) {
        removeAllViews();
        addView(childView);
    }

}
