package com.rena21c.voiceorder.view.widgets;


import android.content.Context;
import android.view.Gravity;

import com.rena21c.voiceorder.R;
import com.rena21c.voiceorder.util.DpToPxConverter;

public class DeliveryAreaTextView extends android.support.v7.widget.AppCompatTextView {

    public DeliveryAreaTextView(Context context) {
        super(context);

        setTextSize(12);

        int paddingDimension = DpToPxConverter.convertDpToPx(5,getResources().getDisplayMetrics());
        setPadding(paddingDimension,
                    paddingDimension,
                    paddingDimension,
                    paddingDimension);

        setGravity(Gravity.CENTER);

        setBackgroundResource(R.drawable.shape_colorful_border);
    }

    public void setDeliverArea(String area) {
        setText(area);
    }
}
