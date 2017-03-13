package com.rena21c.voiceorder.view.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rena21c.voiceorder.R;
import com.rena21c.voiceorder.model.Item;

import java.util.ArrayList;
import java.util.List;

public class OrderDetailRecyclerAdapter extends RecyclerView.Adapter<OrderDetailRecyclerAdapter.OrderDetailViewHolder>{

    class OrderDetailViewHolder extends RecyclerView.ViewHolder {

        private TextView tvItem;
        private TextView tvVendorName;

        public OrderDetailViewHolder(View itemView) {
            super(itemView);
            tvItem = (TextView)itemView.findViewById(R.id.tvItem);
            tvVendorName = (TextView)itemView.findViewById(R.id.tvVendorName);
        }

        public void bind(Item item) {
            tvItem.setText(item.itemName + " " + item.count + item.unit);
            tvVendorName.setText(item.vendorName);
        }
    }

    List<Item> itemList = new ArrayList<>();

    public OrderDetailRecyclerAdapter(ArrayList<Item> itemList) {
        this.itemList = itemList;
    }

    @Override
    public OrderDetailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_detail, parent, false);
        return new OrderDetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(OrderDetailViewHolder holder, int position) {
        Item item = itemList.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
