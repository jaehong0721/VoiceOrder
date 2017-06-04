package com.rena21c.voiceorder.view.adapters;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rena21c.voiceorder.R;
import com.rena21c.voiceorder.pojo.Vendor;

import java.util.ArrayList;
import java.util.List;

public class VendorsRecyclerViewAdapter extends RecyclerView.Adapter<VendorsRecyclerViewAdapter.VendorInfoViewHolder>{

    class VendorInfoViewHolder extends RecyclerView.ViewHolder {

        private TextView tvVendorName;
        private TextView tvBusinessContent;
        private TextView tvAddress;

        public VendorInfoViewHolder(View itemView) {
            super(itemView);
            tvVendorName = (TextView) itemView.findViewById(R.id.tvVendorName);
            tvBusinessContent = (TextView) itemView.findViewById(R.id.tvBusinessContent);
            tvAddress = (TextView) itemView.findViewById(R.id.tvAddress);
        }

        public void bind(String vendorName, String businessContent, String address) {
            tvVendorName.setText(vendorName);
            tvBusinessContent.setText(businessContent);
            tvAddress.setText(address);
        }
    }

    List<Vendor> vendors;

    public VendorsRecyclerViewAdapter() {
        vendors = new ArrayList<>();
    }

    @Override public VendorInfoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vendors, parent, false);
        return new VendorInfoViewHolder(view);
    }

    @Override public void onBindViewHolder(VendorInfoViewHolder holder, int position) {
        Vendor vendor = vendors.get(position);
        String vendorName = vendor.name;
        String businessContent = vendor.items;
        String address = transformToSimpleAddress(vendor.address);
        holder.bind(vendorName, businessContent, address);
    }

    @Override public int getItemCount() {
        return vendors.size();
    }

    public void setVendors(List<Vendor> vendors) {
        this.vendors = vendors;
        notifyDataSetChanged();
    }

    private String transformToSimpleAddress(String address) {
        String[] array = address.split(" ");

        String state = array[0];
        String locality = array[1];

        state = state.substring(0,2);

        return state + " " + locality;
    }
    
}
