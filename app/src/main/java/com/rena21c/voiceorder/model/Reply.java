package com.rena21c.voiceorder.model;


import java.util.ArrayList;

public class Reply {
    public ArrayList<RepliedEstimateItem> repliedItems;
    public long timeMillis;
    public String vendorName;
    public int totalPrice;

    @Override public String toString() {
        return "Reply{" +
                "repliedItems=" + repliedItems +
                ", timeMillis=" + timeMillis +
                ", vendorName='" + vendorName + '\'' +
                ", totalPrice=" + totalPrice +
                '}';
    }

    public float getReplyRate() {
        int hasReplyCount = getReplyCount();

        return (float) hasReplyCount / repliedItems.size();
    }

    public String getReplyRateString() {
        int hasReplyCount = getReplyCount();
        return hasReplyCount == repliedItems.size() ? "총" : repliedItems.size() + "중 " + hasReplyCount + "개";
    }

    private int getReplyCount() {
        int hasReplyCount = 0;

        for(int i = 0; i<repliedItems.size(); i++) {
            if(repliedItems.get(i).price != 0)
                hasReplyCount++;
        }

        return hasReplyCount;
    }
}
