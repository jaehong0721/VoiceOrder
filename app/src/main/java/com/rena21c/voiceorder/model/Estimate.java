package com.rena21c.voiceorder.model;


import java.util.ArrayList;
import java.util.HashMap;

public class Estimate {
    public String restaurantAddress;
    public String restaurantName;
    public ArrayList<RequestedEstimateItem> items;
    public HashMap<String, Reply> reply;

    public ArrayList<String> makeItemNameList() {
        ArrayList<String> itemNames = new ArrayList<>();
        for(int i = 0; i<items.size(); i++) {
            itemNames.add(items.get(i).itemName);
        }
        return itemNames;
    }

    public ArrayList<String> makeItemNumList() {
        ArrayList<String> itemNums = new ArrayList<>();
        for(int i = 0; i<items.size(); i++) {
            itemNums.add(items.get(i).itemNum);
        }
        return itemNums;
    }
}
