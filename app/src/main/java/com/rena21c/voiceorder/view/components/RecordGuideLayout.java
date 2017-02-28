package com.rena21c.voiceorder.view.components;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.rena21c.voiceorder.R;


public class RecordGuideLayout extends FrameLayout {

    public RecordGuideLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.layout_component_record_guide, this);
    }

}
