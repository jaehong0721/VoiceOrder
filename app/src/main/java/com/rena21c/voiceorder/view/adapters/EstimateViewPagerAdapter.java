package com.rena21c.voiceorder.view.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rena21c.voiceorder.R;
import com.rena21c.voiceorder.etc.PriceComparatorOnEstimate;
import com.rena21c.voiceorder.etc.TimeComparatorOnEstimate;
import com.rena21c.voiceorder.model.RepliedEstimateItem;
import com.rena21c.voiceorder.model.Reply;
import com.rena21c.voiceorder.view.DividerItemDecoration;
import com.rena21c.voiceorder.view.widgets.CallButton;
import com.rena21c.voiceorder.view.widgets.CurrencyFormatTextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;


public class EstimateViewPagerAdapter extends PagerAdapter {

    public interface ClickFinishButtonListener {
        void onClickFinish(String what, String pageKey);
    }

    private final ClickFinishButtonListener listener;

    private ArrayList<String> keyList = new ArrayList<>();
    private HashMap<String, Reply> replyHashMap = new HashMap<>();

    private boolean orderByPrice = true;
    private boolean orderByTime = false;

    public EstimateViewPagerAdapter(boolean isFinish, ClickFinishButtonListener listener) {
        this.listener = listener;

        if(isFinish)
            keyList.add("end");
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View view;
        final Context context = container.getContext();
        if(keyList.get(position).equals("end")) {
            view = View.inflate(context, R.layout.item_viewpager_estimate_new_request, null);
            Button btnRequestEstimate = (Button) view.findViewById(R.id.btnRequestEstimate);
            btnRequestEstimate.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onClickFinish("request", null);
                }
            });
        } else {
            view = View.inflate(context, R.layout.item_viewpager_estimate_reply, null);
            final Reply reply = replyHashMap.get(keyList.get(position));
            final ArrayList<RepliedEstimateItem> repliedItems = reply.repliedItems;

            TextView tvReplyRate = (TextView) view.findViewById(R.id.tvReplyRate);
            tvReplyRate.setText(reply.getReplyRateString() + " 견적금액");

            CurrencyFormatTextView tvPrice = (CurrencyFormatTextView) view.findViewById(R.id.tvPrice);
            tvPrice.setWon(reply.totalPrice);

            TextView tvVendorName = (TextView) view.findViewById(R.id.tvVendorName);
            tvVendorName.setText(reply.vendorName);

            CallButton ivCall = (CallButton) view.findViewById(R.id.ivCall);
            ivCall.setCalleeInfo(keyList.get(position), reply.vendorName);

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

                    CurrencyFormatTextView tvPrice = (CurrencyFormatTextView) holder.itemView.findViewById(R.id.tvPrice);
                    tvPrice.setWon(repliedItems.get(position).price);
                }

                @Override public int getItemCount() {
                    return repliedItems.size();
                }
            });

            RelativeLayout btnVoiceOrder= (RelativeLayout) view.findViewById(R.id.btnVoiceOrder);
            final Button btnFinish = (Button) view.findViewById(R.id.btnFinish);

            if(reply.isPicked){
                btnVoiceOrder.setVisibility(View.VISIBLE);
                btnFinish.setVisibility(View.GONE);

                btnVoiceOrder.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) {
                        listener.onClickFinish("order", keyList.get(position));
                    }
                });
            } else {
                btnFinish.setVisibility(View.VISIBLE);
                btnVoiceOrder.setVisibility(View.GONE);

                btnFinish.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) {
                        listener.onClickFinish("finish", keyList.get(position));
                    }
                });
            }
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

    public int addReply(String replyKey, Reply reply) {
        keyList.add(replyKey);
        replyHashMap.put(replyKey, reply);

        sortKeyList(orderByPrice, orderByTime);

        notifyDataSetChanged();

        return keyList.indexOf(replyKey);
    }

    public int changeReply(String key, Reply reply) {
        replyHashMap.put(key, reply);

        sortKeyList(orderByPrice, orderByTime);

        notifyDataSetChanged();

        return keyList.indexOf(key);
    }

    public int pickedReply(String key, Reply reply) {
        keyList.clear();
        keyList.add(key);
        keyList.add("end");

        replyHashMap.clear();
        replyHashMap.put(key, reply);

        notifyDataSetChanged();

        return keyList.indexOf(key);
    }

    public void setSorting(boolean orderByPrice, boolean orderByTime) {
        this.orderByPrice = orderByPrice;
        this.orderByTime = orderByTime;

        sortKeyList(orderByPrice, orderByTime);
        notifyDataSetChanged();
    }

    private void sortKeyList(boolean orderByPrice, boolean orderByTime) {
        if(keyList.size() == 0) return;
        if(orderByPrice) {
            PriceComparatorOnEstimate priceComparatorOnEstimate = new PriceComparatorOnEstimate(replyHashMap);
            Collections.sort(keyList, priceComparatorOnEstimate);
        } else if(orderByTime){
            TimeComparatorOnEstimate timeComparatorOnEstimate = new TimeComparatorOnEstimate(replyHashMap);
            Collections.sort(keyList, timeComparatorOnEstimate);
        } else {
            throw new RuntimeException("가격순, 시간순 정렬 중 하나여야 합니다.");
        }
    }
}
