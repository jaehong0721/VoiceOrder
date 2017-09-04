package com.rena21c.voiceorder.view.widgets;


import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.rena21c.voiceorder.R;
import com.rena21c.voiceorder.util.DpToPxConverter;

public class EstimateInputView extends LinearLayout {

    private EditText etItemName;
    private EditText etItemNum;

    public EstimateInputView(Context context) {
        this(context,null);
    }

    public EstimateInputView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        setOrientation(LinearLayout.HORIZONTAL);
        int topPadding = DpToPxConverter.convertDpToPx(12, getResources().getDisplayMetrics());
        setPadding(0,topPadding,0,0);

        initItemNameView(context, attrs);
        initItemNumView(context, attrs);

        addView(etItemName);
        addView(etItemNum);
    }

    public String getItemName() {
        String itemName = etItemName.getText().toString();
        return itemName.equals("") ? null : itemName;
    }

    public String getItemNum() {
        String itemNum = etItemNum.getText().toString();
        return itemNum.equals("") ? null : itemNum;
    }

    private void initItemNameView(Context context, AttributeSet attrs) {
        etItemName = newInputEditText(context, attrs);
        ((LayoutParams)etItemName.getLayoutParams()).weight = 2;
        etItemName.setPadding(DpToPxConverter.convertDpToPx(14,getResources().getDisplayMetrics()),0,0,0);
        etItemName.setHint("품목명");
        etItemName.setImeOptions(EditorInfo.IME_ACTION_NEXT);
    }

    private void initItemNumView(Context context, AttributeSet attrs) {
        etItemNum = newInputEditText(context, attrs);
        ((LayoutParams)etItemNum.getLayoutParams()).weight = 1;
        ((LayoutParams)etItemNum.getLayoutParams()).leftMargin = DpToPxConverter.convertDpToPx(4, getResources().getDisplayMetrics());
        etItemNum.setPadding(DpToPxConverter.convertDpToPx(17,getResources().getDisplayMetrics()),0,0,0);
        etItemNum.setHint("기본단가");
        etItemNum.setImeOptions(EditorInfo.IME_ACTION_UNSPECIFIED);
    }

    private EditText newInputEditText(Context context, AttributeSet attrs) {
        EditText editText = new EditText(context, attrs);

        int height = DpToPxConverter.convertDpToPx(48, getResources().getDisplayMetrics());
        LayoutParams lp = new LayoutParams(0,height);
        editText.setLayoutParams(lp);

        editText.setBackground(ContextCompat.getDrawable(context, R.color.black_05));
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        editText.setTextSize(16);
        editText.setHintTextColor(ContextCompat.getColor(context, R.color.dark_maroon_20));

        return editText;
    }
}
