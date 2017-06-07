package com.rena21c.voiceorder.view.widgets;


import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

public class CharacterWrapTextView extends android.support.v7.widget.AppCompatTextView {

    public CharacterWrapTextView(Context context) {
        super(context);
    }

    public CharacterWrapTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CharacterWrapTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override public void setText(CharSequence text, BufferType type) {
        super.setText(text.toString().replace(" ", "\u00A0"), type);
    }
}
