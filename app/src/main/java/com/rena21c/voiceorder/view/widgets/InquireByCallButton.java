package com.rena21c.voiceorder.view.widgets;


import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;

import com.rena21c.voiceorder.R;

public class InquireByCallButton extends android.support.v7.widget.AppCompatButton implements View.OnClickListener {

    private final String phoneNumber = "0230122888";

    public InquireByCallButton(Context context) {
        super(context, null);
    }

    public InquireByCallButton(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public InquireByCallButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.InquireByCallButton);

        String text = typedArray.getString(R.styleable.InquireByCallButton_text);
        setText(text);

        float textSize = typedArray.getFloat(R.styleable.InquireByCallButton_textSize, -1);
        setTextSize(textSize);

        int textColor = typedArray.getColor(R.styleable.InquireByCallButton_textColor, -1);
        setTextColor(textColor);

        Drawable background = typedArray.getDrawable(R.styleable.InquireByCallButton_background);
        setBackground(background);

        typedArray.recycle();

        setOnClickListener(this);
    }

    @SuppressWarnings("MissingPermission")
    @Override public void onClick(View v) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        getContext().startActivity(intent);
    }
}
