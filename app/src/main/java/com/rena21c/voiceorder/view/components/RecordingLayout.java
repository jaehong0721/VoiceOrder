package com.rena21c.voiceorder.view.components;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.rena21c.voiceorder.R;

public class RecordingLayout extends RelativeLayout {

    private static RecordingLayout INSTANCE;
    private View view;

    private RecordingLayout(Context context, ViewGroup rootView) {
        super(context);
        init(context, rootView);
    }

    private void init(Context context, ViewGroup rootView) {
        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = layoutInflater.inflate(R.layout.layout_component_recording, rootView, false);
    }

    public static RecordingLayout getInstance(Context context, ViewGroup rootView) {
        if(INSTANCE == null) {
            INSTANCE = new RecordingLayout(context, rootView);
            return INSTANCE;
        }
        else {
            return INSTANCE;
        }
    }

    public View getView() {
        return view;
    }
}
