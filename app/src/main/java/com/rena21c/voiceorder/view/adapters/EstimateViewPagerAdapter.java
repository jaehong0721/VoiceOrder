package com.rena21c.voiceorder.view.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rena21c.voiceorder.R;
import com.rena21c.voiceorder.etc.PriceComparatorOnEstimate;
import com.rena21c.voiceorder.model.RepliedEstimateItem;
import com.rena21c.voiceorder.model.Reply;
import com.rena21c.voiceorder.view.DividerItemDecoration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;


public class EstimateViewPagerAdapter extends PagerAdapter {

    private ArrayList<String> keyList = new ArrayList<>();
    private HashMap<String, Reply> replyHashMap = new HashMap<>();
    private boolean isFinish;

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view;
        final Context context = container.getContext();
        if(position == keyList.size()-1 && isFinish) {
            view = View.inflate(context, R.layout.item_viewpager_estimate, null);
        } else {
            view = View.inflate(context, R.layout.item_viewpager_estimate, null);
            final Reply reply = replyHashMap.get(keyList.get(position));
            final ArrayList<RepliedEstimateItem> repliedItems = reply.repliedItems;

            TextView tvReplyRate = (TextView) view.findViewById(R.id.tvReplyRate);
            tvReplyRate.setText(reply.getReplyRateString() + " 견적금액");

            TextView tvPrice = (TextView) view.findViewById(R.id.tvPrice);
            tvPrice.setText(String.valueOf(reply.totalPrice));

            TextView tvVendorName = (TextView) view.findViewById(R.id.tvVendorName);
            tvVendorName.setText(reply.vendorName);

            RecyclerView rvRepliedEstimateItem = (RecyclerView) view.findViewById(R.id.rvRepliedEstimateItem);
            rvRepliedEstimateItem.setLayoutManager(new LinearLayoutManager(context));
            rvRepliedEstimateItem.addItemDecoration(new DividerItemDecoration(context, R.drawable.shape_divider_recycler_view));
            rvRepliedEstimateItem.setAdapter(new RecyclerView.Adapter() {
                @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                    View itemView = View.inflate(parent.getContext(), R.layout.item_replied_estimate_item, null);
                    return new RecyclerView.ViewHolder(itemView) {};
                }

                @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                    TextView tvItemInfo = (TextView) holder.itemView.findViewById(R.id.tvItemInfo);
                    String itemInfo = repliedItems.get(position).itemName + "," + repliedItems.get(position).itemNum;
                    tvItemInfo.setText(itemInfo);

                    TextView tvPrice = (TextView) holder.itemView.findViewById(R.id.tvPrice);
                    tvPrice.setText(String.valueOf(repliedItems.get(position).price));
                }

                @Override public int getItemCount() {
                    return repliedItems.size();
                }
            });
        }
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        container.removeView(view);
    }

    @Override
    public int getCount() {
        return keyList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public void addReply(String replyKey, Reply reply) {
        keyList.add(replyKey);
        replyHashMap.put(replyKey, reply);

        PriceComparatorOnEstimate priceComparatorOnEstimate = new PriceComparatorOnEstimate(replyHashMap);
        Collections.sort(keyList, priceComparatorOnEstimate);

        notifyDataSetChanged();
    }

    public void changeReply(String key, Reply reply) {
        replyHashMap.put(key, reply);

        PriceComparatorOnEstimate priceComparatorOnEstimate = new PriceComparatorOnEstimate(replyHashMap);
        Collections.sort(keyList, priceComparatorOnEstimate);

        notifyDataSetChanged();
    }
}