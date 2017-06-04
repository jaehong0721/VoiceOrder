package com.rena21c.voiceorder.view.adapters;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rena21c.voiceorder.R;
import com.rena21c.voiceorder.pojo.Vendor;

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
        private ImageView ivCalll;

        public VendorInfoViewHolder(View itemView) {
            super(itemView);
            tvVendorName = (TextView) itemView.findViewById(R.id.tvVendorName);
            tvBusinessContent = (TextView) itemView.findViewById(R.id.tvBusinessContent);
            tvAddress = (TextView) itemView.findViewById(R.id.tvAddress);
            ivCalll = (ImageView) itemView.findViewById(R.id.ivCall);
        }

        public void bind(String vendorName, String businessContent, String address, View.OnClickListener clickListener) {
            tvVendorName.setText(vendorName);
            tvBusinessContent.setText(businessContent);
            tvAddress.setText(address);
            ivCalll.setOnClickListener(clickListener);
        }
    }

    List<Vendor> vendors;

    CallButtonClickListener callButtonClickListener;

    public VendorsRecyclerViewAdapter(CallButtonClickListener callButtonClickListener) {
        vendors = new ArrayList<>();
        this.callButtonClickListener = callButtonClickListener;
    }

    @Override public VendorInfoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vendors, parent, false);
        return new VendorInfoViewHolder(view);
    }

    @Override public void onBindViewHolder(VendorInfoViewHolder holder, int position) {
        Vendor vendor = vendors.get(position);

        String vendorName = vendor.name;
        String address = transformToSimpleAddress(vendor.address);
        String businessContent = removeAddressAndNameInContent(vendor.items,  vendorName, address);

        final String phoneNumber = vendor.phoneNumber;
        holder.bind(vendorName, businessContent, address, new View.OnClickListener() {
            @Override public void onClick(View v) {
                callButtonClickListener.onCallButtonClick(phoneNumber);
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
