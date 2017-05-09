package com.rena21c.voiceorder.viewmodel;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.rena21c.voiceorder.R;
import com.rena21c.voiceorder.model.OrderItem;
import com.rena21c.voiceorder.model.VoiceRecord;
import com.rena21c.voiceorder.util.FileNameUtil;

import java.util.ArrayList;
import java.util.HashMap;

public class AcceptedOrderPage extends OrderPage {

    private final HashMap<String, VoiceRecord> itemHashMap;

    public AcceptedOrderPage(String timeStamp, HashMap<String, VoiceRecord> itemHashMap) {
        super(timeStamp);
        this.itemHashMap = itemHashMap;
    }

    @Override public View getView(LayoutInflater layoutInflater, final OnClickDetailsOrderPageListener onClickListener) {
        View view = layoutInflater.inflate(R.layout.after_accept_order_view, null, false);
        TextView tvTimeStamp = (TextView) view.findViewById(R.id.tvTimeStamp);
        TextView tvItemList = (TextView) view.findViewById(R.id.tvItemList);
        TextView tvVendorList = (TextView) view.findViewById(R.id.tvVendorList);

        String displayTime = FileNameUtil.getDisplayTimeFromfileName(timeStamp);
        tvTimeStamp.setText(displayTime);

        tvItemList.setText(makeItemList());
        tvVendorList.setText(makeVendorList());

        Button btnGoDetail = (Button) view.findViewById(R.id.btnGoDetail);
        btnGoDetail.setOnClickListener(new OnClickListener() {
            @Override public void onClick(View v) {
                onClickListener.onClickDetailsOrderPage(timeStamp, itemHashMap);
            }
        });
        return view;
    }


    private String makeVendorList() {
        StringBuffer sb = new StringBuffer();

        for (HashMap.Entry<String, VoiceRecord> entry : itemHashMap.entrySet()) {
            sb.append(entry.getKey());
            if (entry.getValue().accepted) {
                sb.append("(주문확인)");
            }
            sb.append(",");

        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    private String makeItemList() {
        StringBuffer sb = new StringBuffer();
        ArrayList<OrderItem> items = new ArrayList<>();
        Log.d("", "itemHashMap.values()" + itemHashMap.toString());
        for (VoiceRecord voiceRecord : itemHashMap.values()) {
            items.addAll(voiceRecord.orderItems);
        }
        for (OrderItem item : items) {
            sb.append(item.name)
                    .append(" ")
                    .append(item.count)
                    .append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }
}
