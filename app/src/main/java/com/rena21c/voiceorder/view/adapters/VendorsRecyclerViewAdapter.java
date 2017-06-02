package com.rena21c.voiceorder.view.adapters;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;

import com.rena21c.voiceorder.R;
import com.rena21c.voiceorder.pojo.Vendor;

import java.util.ArrayList;
import java.util.List;

public class VendorsRecyclerViewAdapter extends RecyclerView.Adapter<VendorsRecyclerViewAdapter.VendorInfoViewHolder>{

    class VendorInfoViewHolder extends RecyclerView.ViewHolder {

        private TextView tvVendorName;
        private TextView tvBusinessContent;

        private GridLayout deliveryAreasContainer;

        public VendorInfoViewHolder(View itemView) {
            super(itemView);
            tvVendorName = (TextView) itemView.findViewById(R.id.tvVendorName);
            tvBusinessContent = (TextView) itemView.findViewById(R.id.tvBusinessContent);
            deliveryAreasContainer = (GridLayout) itemView.findViewById(R.id.deliveryAreasContainer);
        }

        public void bind(String vendorName, String businessContent) {
            tvVendorName.setText(vendorName);
            tvBusinessContent.setText(businessContent);
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
        holder.bind(vendorName, businessContent);
    }

    @Override public int getItemCount() {
        return vendors.size();
    }

    public void setVendors(List<Vendor> vendors) {
        this.vendors = vendors;
        notifyDataSetChanged();
    }
}
