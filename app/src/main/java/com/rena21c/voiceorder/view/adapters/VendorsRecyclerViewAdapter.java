package com.rena21c.voiceorder.view.adapters;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rena21c.voiceorder.R;
import com.rena21c.voiceorder.etc.AppPreferenceManager;
import com.rena21c.voiceorder.pojo.Vendor;
import com.rena21c.voiceorder.util.AddressUtil;
import com.rena21c.voiceorder.util.TimeConverter;
import com.rena21c.voiceorder.viewholder.VendorInfoViewHolder;

import java.util.ArrayList;
import java.util.List;

public class VendorsRecyclerViewAdapter extends RecyclerView.Adapter<VendorInfoViewHolder>{

    List<Vendor> vendors;

    VendorInfoViewHolder.CallButtonClickListener callButtonClickListener;

    AppPreferenceManager appPreferenceManager;

    public VendorsRecyclerViewAdapter(AppPreferenceManager appPreferenceManager,
                                      VendorInfoViewHolder.CallButtonClickListener callButtonClickListener) {
        vendors = new ArrayList<>();
        this.appPreferenceManager = appPreferenceManager;
        this.callButtonClickListener = callButtonClickListener;
    }

    @Override public VendorInfoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vendors, parent, false);
        return new VendorInfoViewHolder(view);
    }

    @Override public void onBindViewHolder(final VendorInfoViewHolder holder, final int position) {
        Vendor vendor = vendors.get(position);

        final String phoneNumber = vendor.phoneNumber;

        final String vendorName = vendor.name;
        String address = AddressUtil.convertToSimpleAddress(vendor.address);
        String businessContent = removeAddressAndNameInContent(vendor.items,  vendorName, address);

        long callTime = appPreferenceManager.getCallTime(phoneNumber);
        String elapsedTime = callTime == -1 ? null : TimeConverter.convert(System.currentTimeMillis(), callTime) + " 통화";

        holder.bindVendorName(vendorName)
                .bindAddress(address)
                .bindBusinessContent(businessContent)
                .bindElapsedTimeFromCall(elapsedTime)
                .setCallButtonClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) {
                        callButtonClickListener.onCallButtonClick(phoneNumber, vendorName, position);
                    }
                });
    }

    @Override public int getItemCount() {
        return vendors.size();
    }

    public void setVendors(List<Vendor> vendors) {
        this.vendors= vendors;
        notifyDataSetChanged();
    }

    public void clearVendors() {
        vendors.clear();
    }

    private String removeAddressAndNameInContent(String content, String name, String address) {
        return content.replaceAll(name, "").replaceAll(address, "");
    }
}
