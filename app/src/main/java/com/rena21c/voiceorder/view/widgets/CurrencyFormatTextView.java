package com.rena21c.voiceorder.view.widgets;


import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import java.util.Locale;

public class CurrencyFormatTextView extends android.support.v7.widget.AppCompatTextView{

    public CurrencyFormatTextView(Context context) {
        this(context, null);
    }

    public CurrencyFormatTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setWon(long amount) {
        super.setText(String.format(Locale.KOREA, "%,d", amount));
    }
}
