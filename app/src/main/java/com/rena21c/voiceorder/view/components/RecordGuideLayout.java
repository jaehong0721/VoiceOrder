package com.rena21c.voiceorder.view.components;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.rena21c.voiceorder.R;


public class RecordGuideLayout extends RelativeLayout {

    private static RecordGuideLayout INSTANCE;
    private static View view;

    public RecordGuideLayout(Context context, ViewGroup rootView) {
        super(context);
        init(context,rootView);
    }

    private void init(Context context, ViewGroup rootView) {
        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = layoutInflater.inflate(R.layout.layout_component_record_guide, rootView, false);
    }

    public static RecordGuideLayout getInstance(Context context, ViewGroup rootView) {
        if(INSTANCE == null) {
            INSTANCE = new RecordGuideLayout(context, rootView);
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
