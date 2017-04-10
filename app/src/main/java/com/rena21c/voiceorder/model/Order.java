package com.rena21c.voiceorder.model;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class Order {


    public enum OrderState {
        IN_PROGRESS, ACCEPTED, FAILED
    }

    public String timeStamp;
    public HashMap<String, VoiceRecord> itemHashMap;
    public OrderState orderState;

    public Order(OrderState orderState, String timeStamp, HashMap<String, VoiceRecord> itemHashMap) {
        this.orderState = orderState;
        this.timeStamp = timeStamp;
        this.itemHashMap = itemHashMap;
    }

    public String makeVendorList() {
        StringBuffer sb = new StringBuffer();

        for (HashMap.Entry<String, VoiceRecord> entry : itemHashMap.entrySet()) {
            sb.append(entry.getKey()).append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    public String makeItemList() {
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

    public boolean match(String timeStamp) {
        return timeStamp.equals(timeStamp);
    }
}
