package com.rena21c.voiceorder.model;


public class RepliedEstimateItem extends RequestedEstimateItem {
    public int price;

    @Override public String toString() {
        return "RepliedEstimateItem{" +
                "price=" + price +
                '}';
    }
}
