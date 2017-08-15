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
import com.rena21c.voiceorder.util.AddressUtil;
import com.rena21c.voiceorder.util.TimeConverter;

import java.util.ArrayList;
import java.util.List;

public class VendorsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    public interface CallButtonClickListener {
        void onCallButtonClick(String phoneNumber, String name, int itemPosition);
    }

    public interface AppDownloadListener {
        void onAppDownload();
    }

    public interface ClickVendorListener {
        void onClickVendor(String phoneNumber);
    }

    private class VendorInfoViewHolder extends RecyclerView.ViewHolder {

        private TextView tvVendorName;
        private TextView tvBusinessContent;
        private TextView tvAddress;
        private TextView tvElapsedTimeFromCall;
        private ImageView ivCall;

        VendorInfoViewHolder(View itemView) {
            super(itemView);
            tvVendorName = (TextView) itemView.findViewById(R.id.tvVendorName);
            tvBusinessContent = (TextView) itemView.findViewById(R.id.tvBusinessContent);
            tvAddress = (TextView) itemView.findViewById(R.id.tvAddress);
            tvElapsedTimeFromCall = (TextView) itemView.findViewById(R.id.tvElapsedTimeFromCall);
            ivCall = (ImageView) itemView.findViewById(R.id.ivCall);
        }

        VendorInfoViewHolder bindVendorName(String vendorName) {tvVendorName.setText(vendorName); return this;}

        VendorInfoViewHolder bindBusinessContent(String businessContent) {tvBusinessContent.setText(businessContent); return this;}

        VendorInfoViewHolder bindAddress(String address) {tvAddress.setText(address); return this;}

        VendorInfoViewHolder bindElapsedTimeFromCall(String elapsedTime) {
            if(elapsedTime != null) {
                tvElapsedTimeFromCall.setText(elapsedTime);
                tvElapsedTimeFromCall.setVisibility(View.VISIBLE);
            } else {
                tvElapsedTimeFromCall.setVisibility(View.GONE);
            }
            return this;
        }

        VendorInfoViewHolder setCallButtonClickListener(View.OnClickListener clickListener) {
            ivCall.setOnClickListener(clickListener);
            return this;
        }

        VendorInfoViewHolder setClickVendorListener(View.OnClickListener clickListener) {
            itemView.setOnClickListener(clickListener);
            return this;
        }
    }

    private class VendorAppDownloadViewHolder extends RecyclerView.ViewHolder {

        public VendorAppDownloadViewHolder(View itemView) {
            super(itemView);
        }

        void setAppDownloadListener(final AppDownloadListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onAppDownload();
                }
            });
        }
    }


    private List<Vendor> vendors;
    private AppPreferenceManager appPreferenceManager;
    private final CallButtonClickListener callButtonClickListener;
    private final AppDownloadListener appDownloadListener;
    private final ClickVendorListener clickVendorListener;

    public VendorsRecyclerViewAdapter(AppPreferenceManager appPreferenceManager,
                                      CallButtonClickListener callButtonClickListener,
                                      AppDownloadListener appDownloadListener,
                                      ClickVendorListener clickVendorListener) {
        vendors = new ArrayList<>();
        this.appPreferenceManager = appPreferenceManager;
        this.callButtonClickListener = callButtonClickListener;
        this.appDownloadListener = appDownloadListener;
        this.clickVendorListener = clickVendorListener;
    }

    @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if(viewType == 1) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_download_vendor_app, parent, false);
            return new VendorAppDownloadViewHolder(view);
        }
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vendors, parent, false);
        return new VendorInfoViewHolder(view);
    }

    @Override public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof VendorInfoViewHolder) {
            Vendor vendor = vendors.get(position);

            final String phoneNumber = vendor.phoneNumber;

            final String vendorName = vendor.name;
            String address = AddressUtil.convertToSimpleAddress(vendor.address);
            String businessContent = vendor.items;

            long callTime = appPreferenceManager.getCallTime(phoneNumber);
            String elapsedTime = callTime == -1 ? null : TimeConverter.convertMillisToElapsedTime(System.currentTimeMillis(), callTime) + " 통화";

            ((VendorInfoViewHolder)holder)
                    .bindVendorName(vendorName)
                    .bindAddress(address)
                    .bindBusinessContent(businessContent)
                    .bindElapsedTimeFromCall(elapsedTime)
                    .setCallButtonClickListener(new View.OnClickListener() {
                        @Override public void onClick(View v) {
                            callButtonClickListener.onCallButtonClick(phoneNumber, vendorName, holder.getAdapterPosition());
                        }
                    })
                    .setClickVendorListener(new View.OnClickListener() {
                        @Override public void onClick(View v) {
                            clickVendorListener.onClickVendor(phoneNumber);
                        }
                    });
        } else {
            ((VendorAppDownloadViewHolder)holder).setAppDownloadListener(appDownloadListener);
        }
    }

    @Override public int getItemCount() {
        return vendors.size();
    }

    @Override public int getItemViewType(int position) {
        return position % 10;
    }

    public void setVendors(List<Vendor> vendors) {
        this.vendors= vendors;
        notifyDataSetChanged();
    }

    public void clearVendors() {
        vendors.clear();
    }
}
