package com.rena21c.voiceorder.viewholder;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.rena21c.voiceorder.R;
import com.rena21c.voiceorder.view.adapters.MyPartnersRecyclerViewAdapter;

public class SimpleVendorInfoViewHolder extends RecyclerView.ViewHolder {

    private TextView tvVendorName;
    private TextView tvElapsedTimeFromCall;

    public SimpleVendorInfoViewHolder(View itemView, final RecyclerView.Adapter adapter) {
        super(itemView);

        tvVendorName = (TextView) itemView.findViewById(R.id.tvVendorName);
        tvElapsedTimeFromCall = (TextView) itemView.findViewById(R.id.tvElapsedTimeFromCall);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                ((MyPartnersRecyclerViewAdapter)adapter).itemClicked(getAdapterPosition());
            }
        });
    }

    public void bind(String vendorName, String elapsedTime) {
        tvVendorName.setText(vendorName);
        tvElapsedTimeFromCall.setText(elapsedTime);
    }
}

