package com.rena21c.voiceorder.model;

import java.util.ArrayList;

public class Order {

    public String timeStamp;
    public ArrayList<Item> items;

    public Order(String timeStamp, ArrayList<Item> items) {
        this.timeStamp = timeStamp;
        this.items = items;
    }

    public String makeItemList() {
        StringBuffer stringBuffer = new StringBuffer();
        for(int i = 0; i<items.size(); i++) {
            stringBuffer.append(items.get(i).itemName)
                    .append(" ")
                    .append(items.get(i).count)
                    .append(items.get(i).unit);
            if(i != items.size()-1) {
                stringBuffer.append(", ");
            }
        }
        return stringBuffer.toString();
    }

    public String makeVendorList() {
        StringBuffer stringBuffer = new StringBuffer();

        for(int i = 0; i<items.size(); i++) {
            stringBuffer.append(items.get(i).vendorName);
            if(i != items.size()-1) {
                stringBuffer.append(",");
            }
        }
        return stringBuffer.toString();
    }

}
