package com.rena21c.voiceorder.model;


import java.io.Serializable;

public class Item implements Serializable{

    public String itemName;
    public String vendorName;
    public String unit;
    public int count;

    public Item(String itemName, String unit, String vendorName, int count) {
        this.itemName = itemName;
        this.unit = unit;
        this.vendorName = vendorName;
        this.count = count;
    }

}
