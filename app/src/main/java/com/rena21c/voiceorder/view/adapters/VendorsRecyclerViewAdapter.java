package com.rena21c.voiceorder.view.adapters;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rena21c.voiceorder.R;
import com.rena21c.voiceorder.etc.AppPreferenceManager;
import com.rena21c.voiceorder.pojo.Vendor;
import com.rena21c.voiceorder.util.TimeConverter;

import java.util.ArrayList;
import java.util.List;

public class VendorsRecyclerViewAdapter extends RecyclerView.Adapter<VendorsRecyclerViewAdapter.VendorInfoViewHolder>{

    public interface CallButtonClickListener {
        void onCallButtonClick(String phoneNumber);
    }

    class VendorInfoViewHolder extends RecyclerView.ViewHolder {

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

    List<Vendor> vendors;

    CallButtonClickListener callButtonClickListener;

    AppPreferenceManager appPreferenceManager;

    public VendorsRecyclerViewAdapter(AppPreferenceManager appPreferenceManager, CallButtonClickListener callButtonClickListener) {
        vendors = new ArrayList<>();
        this.appPreferenceManager = appPreferenceManager;
        this.callButtonClickListener = callButtonClickListener;
    }

    @Override public VendorInfoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vendors, parent, false);
        return new VendorInfoViewHolder(view);
    }

    @Override public void onBindViewHolder(final VendorInfoViewHolder holder, int position) {
        Vendor vendor = vendors.get(position);

        final String phoneNumber = vendor.phoneNumber;

        String vendorName = vendor.name;
        String address = transformToSimpleAddress(vendor.address);
        String businessContent = removeAddressAndNameInContent(vendor.items,  vendorName, address);

        long callTime = appPreferenceManager.getCallTime(phoneNumber);
        String elapsedTime = callTime == -1 ? null : TimeConverter.convert(System.currentTimeMillis(), callTime) + " 통화";

        holder.bindVendorName(vendorName)
                .bindAddress(address)
                .bindBusinessContent(businessContent)
                .bindElapsedTimeFromCall(elapsedTime)
                .setCallButtonClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) {
                        callButtonClickListener.onCallButtonClick(phoneNumber);
                        holder.bindElapsedTimeFromCall("방금전 통화");
                    }
                });
    }

    @Override public int getItemCount() {
        return vendors.size();
    }

    public void setVendors(List<Vendor> vendors) {
        this.vendors = vendors;
        notifyDataSetChanged();
    }

    private String transformToSimpleAddress(String fullAddress) {
        String[] array = fullAddress.split(" ");

        String state = array[0];
        String locality = array[1];

        state = state.substring(0,2);

        return state + " " + locality;
    }
    
    private String removeAddressAndNameInContent(String content, String name, String address) {
        return content.replaceAll(name, "").replaceAll(address, "");
    }
}
