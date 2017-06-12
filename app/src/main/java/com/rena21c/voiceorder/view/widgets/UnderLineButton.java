package com.rena21c.voiceorder.view.widgets;


import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rena21c.voiceorder.R;

public class UnderLineButton extends LinearLayout {

    private ImageView ivImage;
    private TextView tvText;
    private View underLine;

    private String text;
    private int selectedImage;
    private int unselectedImage;
    private int selectedColor;
    private int unselectedColor;

    public UnderLineButton(Context context) {
        super(context, null);
    }

    public UnderLineButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        inflate(context, R.layout.widget_underline_button, this);
        ivImage = (ImageView) findViewById(R.id.ivImage);
        tvText = (TextView) findViewById(R.id.tvText);
        underLine = findViewById(R.id.underLine);

        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.UnderLineButton);

        text = typedArray.getString(R.styleable.UnderLineButton_text);
        tvText.setText(text);

        selectedColor = typedArray.getColor(R.styleable.UnderLineButton_selectedColor, -1);
        unselectedColor = typedArray.getColor(R.styleable.UnderLineButton_unselectedColor, -1);
        selectedImage = typedArray.getResourceId(R.styleable.UnderLineButton_selectedImage, -1);
        unselectedImage = typedArray.getResourceId(R.styleable.UnderLineButton_unselectedImage, -1);

        typedArray.recycle();
    }

    @Override public void setSelected(boolean selected) {
        if(selected) {
            tvText.setTextColor(selectedColor);
            ivImage.setImageResource(selectedImage);
            underLine.setVisibility(View.VISIBLE);
        } else {
            tvText.setTextColor(unselectedColor);
            ivImage.setImageResource(unselectedImage);
            underLine.setVisibility(View.GONE);
        }
    }
}
