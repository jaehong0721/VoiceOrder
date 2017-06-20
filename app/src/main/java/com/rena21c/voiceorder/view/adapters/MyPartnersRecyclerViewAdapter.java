package com.rena21c.voiceorder.view.adapters;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rena21c.voiceorder.R;
import com.rena21c.voiceorder.model.Partner;
import com.rena21c.voiceorder.util.TimeConverter;
import com.rena21c.voiceorder.viewholder.ItemOfListDividerTextViewHolder;
import com.rena21c.voiceorder.viewholder.SimpleVendorInfoViewHolder;

import java.util.ArrayList;

public class MyPartnersRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Partner> partners;

    private int numberOfMyPartners;

    public MyPartnersRecyclerViewAdapter(int numberOfMyPartners) {
        this.numberOfMyPartners = numberOfMyPartners;
        this.partners = new ArrayList<>();
    }

    @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {

            case 0:
                View listViewType = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_partners, parent, false);
                return new SimpleVendorInfoViewHolder(listViewType);

            case 1:
                View labelViewType = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_partner_divider, parent, false);
                return new ItemOfListDividerTextViewHolder(labelViewType);

            default:
                return null;
        }
    }

    @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case 0:
                if(position >= numberOfMyPartners) position -= 1;

                String vendorPhoneNumber = partners.get(position).vendorPhoneNumber;

                long callTimeMillis = partners.get(position).callTime;

                String elapsedTime = callTimeMillis != 0 ? TimeConverter.convert(System.currentTimeMillis(), callTimeMillis) : "";
                String vendorName = partners.get(position).vendorName;

                ((SimpleVendorInfoViewHolder)holder).bind(vendorName, elapsedTime);
                break;

            case 1:
                ((ItemOfListDividerTextViewHolder)holder).bind("업체추천에서 통화한 업체");
                break;
        }
    }

    @Override public int getItemCount() {
        return partners.size()+1;
    }

    @Override public int getItemViewType(int position) {
        return position == numberOfMyPartners ? 1 : 0;
    }


    public void setPartners(ArrayList<Partner> partners) {
        this.partners = partners;
        notifyDataSetChanged();
    }
}
