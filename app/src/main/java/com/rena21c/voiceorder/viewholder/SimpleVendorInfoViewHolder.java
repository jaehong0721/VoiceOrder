package com.rena21c.voiceorder.viewholder;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.rena21c.voiceorder.R;

public class SimpleVendorInfoViewHolder extends RecyclerView.ViewHolder {

    private TextView tvVendorName;
    private TextView tvElapsedTimeFromCall;

    public SimpleVendorInfoViewHolder(View itemView) {
        super(itemView);

        tvVendorName = (TextView) itemView.findViewById(R.id.tvVendorName);
        tvElapsedTimeFromCall = (TextView) itemView.findViewById(R.id.tvElapsedTimeFromCall);
    }

    public void bind(String vendorName, String elapsedTime) {
        tvVendorName.setText(vendorName);
        tvElapsedTimeFromCall.setText(elapsedTime);
    }
}

