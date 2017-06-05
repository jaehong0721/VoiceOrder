package com.rena21c.voiceorder.viewholder;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.rena21c.voiceorder.R;

public class VendorInfoViewHolder extends RecyclerView.ViewHolder {

    public interface CallButtonClickListener {
        void onCallButtonClick(String phoneNumber, int itemPosition);
    }

    private TextView tvVendorName;
    private TextView tvBusinessContent;
    private TextView tvAddress;
    private TextView tvElapsedTimeFromCall;
    private ImageView ivCall;

    public VendorInfoViewHolder(View itemView) {
        super(itemView);
        tvVendorName = (TextView) itemView.findViewById(R.id.tvVendorName);
        tvBusinessContent = (TextView) itemView.findViewById(R.id.tvBusinessContent);
        tvAddress = (TextView) itemView.findViewById(R.id.tvAddress);
        tvElapsedTimeFromCall = (TextView) itemView.findViewById(R.id.tvElapsedTimeFromCall);
        ivCall = (ImageView) itemView.findViewById(R.id.ivCall);
    }

    public VendorInfoViewHolder bindVendorName(String vendorName) {tvVendorName.setText(vendorName); return this;}

    public VendorInfoViewHolder bindBusinessContent(String businessContent) {tvBusinessContent.setText(businessContent); return this;}

    public VendorInfoViewHolder bindAddress(String address) {tvAddress.setText(address); return this;}

    public VendorInfoViewHolder setCallButtonClickListener(View.OnClickListener clickListener) {
        ivCall.setOnClickListener(clickListener);
        return this;
    }

    public VendorInfoViewHolder bindElapsedTimeFromCall(String elapsedTime) {
        if(elapsedTime != null) {
            tvElapsedTimeFromCall.setText(elapsedTime);
            tvElapsedTimeFromCall.setVisibility(View.VISIBLE);
        } else {
            tvElapsedTimeFromCall.setVisibility(View.GONE);
        }
        return this;
    }
}
