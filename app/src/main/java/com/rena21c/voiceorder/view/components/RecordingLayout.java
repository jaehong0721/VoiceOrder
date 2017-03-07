package com.rena21c.voiceorder.view.components;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.rena21c.voiceorder.R;

public class RecordingLayout extends RelativeLayout {

    private static View view;

    private ImageView ivWave1;

    public RecordingLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.e("RecordingLayout", "constructor");
        ivWave1 = (ImageView)findViewById(R.id.ivWave1);
    }

    public static View getInstance(Context context) {
        if(view == null) {
            Log.e("RecordingLayout", "getInstance");
            view = inflate(context, R.layout.layout_component_recording, null);
            return view;
        }
        else {
            return view;
        }
    }

}
