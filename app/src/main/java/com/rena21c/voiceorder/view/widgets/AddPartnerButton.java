package com.rena21c.voiceorder.view.widgets;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import com.rena21c.voiceorder.R;

public class AddPartnerButton extends android.support.v7.widget.AppCompatButton implements View.OnClickListener{

    public interface AddPartnerListener {
        void onAddPartner();
    }

    private AddPartnerListener addPartnerListener;

    public AddPartnerButton(Context context) {
        super(context, null);
    }

    public AddPartnerButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.AddPartnerButton);

        String text = typedArray.getString(R.styleable.AddPartnerButton_text);
        setText(text);

        float textSize = typedArray.getFloat(R.styleable.AddPartnerButton_textSize, -1);
        setTextSize(textSize);

        int textColor = typedArray.getColor(R.styleable.AddPartnerButton_textColor, -1);
        setTextColor(textColor);

        Drawable background = typedArray.getDrawable(R.styleable.AddPartnerButton_background);
        setBackground(background);

        typedArray.recycle();

        setOnClickListener(this);
    }

    @Override public void onClick(View v) {
        if(addPartnerListener != null) addPartnerListener.onAddPartner();
    }

    public void setAddPartnerListener(AddPartnerListener addPartnerListener) {
        this.addPartnerListener = addPartnerListener;
    }
}
