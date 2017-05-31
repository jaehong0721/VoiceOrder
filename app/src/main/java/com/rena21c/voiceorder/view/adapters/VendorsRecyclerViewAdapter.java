package com.rena21c.voiceorder.view.adapters;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;

import com.rena21c.voiceorder.R;

import java.util.ArrayList;

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

        public void bind(String s) {
            tvVendorName.setText(s);
            tvBusinessContent.setText(s);
        }
    }

    ArrayList<String> dummys;

    public VendorsRecyclerViewAdapter() {
        dummys = new ArrayList<>();
        dummys.add("dummy01");
        dummys.add("dummy02");
        dummys.add("dummy03");
        dummys.add("dummy04");
        dummys.add("dummy05");
        dummys.add("dummy06");
        dummys.add("dummy07");
        dummys.add("dummy08");
        dummys.add("dummy09");
        dummys.add("dummy10");
        dummys.add("dummy11");
        dummys.add("dummy12");
    }

    @Override public VendorInfoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vendors, parent, false);
        return new VendorInfoViewHolder(view);
    }

    @Override public void onBindViewHolder(VendorInfoViewHolder holder, int position) {
        holder.bind(dummys.get(position));
    }

    @Override public int getItemCount() {
        return dummys.size();
    }
}
