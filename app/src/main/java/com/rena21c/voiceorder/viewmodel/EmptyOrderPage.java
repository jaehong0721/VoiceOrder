package com.rena21c.voiceorder.viewmodel;


import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.rena21c.voiceorder.R;
import com.rena21c.voiceorder.util.FileNameUtil;

public class EmptyOrderPage extends OrderPage {

    public EmptyOrderPage(String timeStamp) {
        super(timeStamp);
    }


    @Override public View getView(LayoutInflater layoutInflater, OnClickDetailsOrderPageListener onClickListener) {
        View view = layoutInflater.inflate(R.layout.before_accept_order_view, null, false);
        TextView tvTimeStamp = (TextView) view.findViewById(R.id.tvTimeStamp);
        String displayTime = FileNameUtil.getDisplayTimeFromfileName(timeStamp);
        tvTimeStamp.setText(displayTime);
        return view;
    }

}
