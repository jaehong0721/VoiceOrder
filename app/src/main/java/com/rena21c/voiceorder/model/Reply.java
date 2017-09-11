package com.rena21c.voiceorder.model;


import java.util.ArrayList;

public class Reply {
    public ArrayList<RepliedEstimateItem> repliedItems;
    public long timeMillis;
    public String vendorName;
    public String vendorPhoneNumber;
    public int totalPrice;

    @Override public String toString() {
        return "Reply{" +
                "repliedItems=" + repliedItems +
                ", timeMillis=" + timeMillis +
                ", vendorName='" + vendorName + '\'' +
                ", vendorPhoneNumber='" + vendorPhoneNumber + '\'' +
                ", totalPrice=" + totalPrice +
                '}';
    }

    public String getReplyRate() {
        int hasReplyCount = 0;

        for(int i = 0; i<repliedItems.size(); i++) {
            if(repliedItems.get(i).price != 0)
                hasReplyCount++;
        }
        String replyRate = hasReplyCount == repliedItems.size() ? "총" : repliedItems.size() + "중 " + hasReplyCount + "개";
        return replyRate;
    }
}
