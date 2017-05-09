package com.rena21c.voiceorder.view.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rena21c.voiceorder.R;
import com.rena21c.voiceorder.model.OrderItem;
import com.rena21c.voiceorder.model.VoiceRecord;

import java.util.ArrayList;
import java.util.HashMap;

public class OrderDetailRecyclerAdapter extends RecyclerView.Adapter<OrderDetailRecyclerAdapter.OrderDetailViewHolder>{

    class OrderDetailViewHolder extends RecyclerView.ViewHolder {

        private TextView tvItem;
        private TextView tvVendorName;

        public OrderDetailViewHolder(View itemView) {
            super(itemView);
            tvItem = (TextView)itemView.findViewById(R.id.tvItem);
            tvVendorName = (TextView)itemView.findViewById(R.id.tvVendorName);
        }

        public void bind(String key, ArrayList<OrderItem> orderItems, boolean accepted) {
            StringBuffer sb = new StringBuffer();
            for (OrderItem item : orderItems) {
                sb.append(item.name)
                        .append(" ")
                        .append(item.count)
                        .append(",");
            }
            sb.deleteCharAt(sb.length()-1);
            tvItem.setText(sb.toString());
            tvVendorName.setText( accepted ? key + "(주문확인)" : key);
        }
    }

    HashMap<String, VoiceRecord> itemHashMap;
    ArrayList<String> keyList = new ArrayList<>();

    public OrderDetailRecyclerAdapter(HashMap<String, VoiceRecord> itemHashMap) {
        this.itemHashMap = itemHashMap;
        keyList.addAll(itemHashMap.keySet());
    }

    @Override
    public OrderDetailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_detail, parent, false);
        return new OrderDetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(OrderDetailViewHolder holder, int position) {
        String key = keyList.get(position);
        VoiceRecord voiceRecord = itemHashMap.get(key);

        holder.bind(key, voiceRecord.orderItems, voiceRecord.accepted);
    }

    @Override
    public int getItemCount() {
        return itemHashMap.size();
    }
}
