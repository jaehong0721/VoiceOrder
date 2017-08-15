package com.rena21c.voiceorder.view.widgets;


import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;

import com.google.android.flexbox.FlexboxLayout;
import com.rena21c.voiceorder.R;
import com.rena21c.voiceorder.util.DpToPxConverter;

public class DeliveryAreaView extends android.support.v7.widget.AppCompatTextView {

    public DeliveryAreaView(Context context) {
        this(context, null);
    }

    public DeliveryAreaView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        FlexboxLayout.LayoutParams lp = new FlexboxLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        int marginStart = DpToPxConverter.convertDpToPx(4, getResources().getDisplayMetrics());
        lp.setMarginStart(marginStart);
        setLayoutParams(lp);

        int padding = DpToPxConverter.convertDpToPx(8, getResources().getDisplayMetrics());
        setPadding(padding,padding,padding,padding);

        setBackground(ContextCompat.getDrawable(context, R.drawable.shape_colorful_border));
        setGravity(Gravity.CENTER);
        setTextSize(13);
        //CalligraphyUtils.applyFontToTextView(context,this, getResources().getString(R.string.fonts_path));
    }

    public void setArea(String area) {
        setText(area);
    }
}
