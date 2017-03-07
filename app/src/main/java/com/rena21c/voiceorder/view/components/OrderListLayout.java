package com.rena21c.voiceorder.view.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.rena21c.voiceorder.R;


public class OrderListLayout extends RelativeLayout {

    private static View view;

    public OrderListLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public static View getInstance(Context context) {
        if(view == null) {
            view = inflate(context, R.layout.layout_component_order_list, null);
            return view;
        }
        else {
            return view;
        }
    }

}
