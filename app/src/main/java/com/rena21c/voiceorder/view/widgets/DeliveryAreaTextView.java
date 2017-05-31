package com.rena21c.voiceorder.view.widgets;


import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;

import com.rena21c.voiceorder.R;

public class DeliveryAreaTextView extends android.support.v7.widget.AppCompatTextView {

    public DeliveryAreaTextView(Context context) {
        super(context);

        setTextSize(12);

        setPadding((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics()));

        setGravity(Gravity.CENTER);

        setBackgroundResource(R.drawable.shape_colorful_border);
    }

    public void setDeliverArea(String area) {
        setText(area);
    }
}
