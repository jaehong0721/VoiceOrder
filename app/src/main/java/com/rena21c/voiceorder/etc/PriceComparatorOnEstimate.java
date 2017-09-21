package com.rena21c.voiceorder.etc;


import com.rena21c.voiceorder.model.Reply;

import java.util.Comparator;
import java.util.HashMap;

public class PriceComparatorOnEstimate implements Comparator<String> {

    HashMap<String, Reply> replyHashMap;

    public PriceComparatorOnEstimate(HashMap<String, Reply> replyHashMap) {
        this.replyHashMap = replyHashMap;
    }

    @Override public int compare(String o1, String o2) {
        if(o1.equals("end")) return 1;
        if(o2.equals("end")) return -1;

        Reply reply1 = replyHashMap.get(o1);
        Reply reply2 = replyHashMap.get(o2);

        float replyRateOfReply1 = reply1.getReplyRate();
        float replyRateOfReply2 = reply2.getReplyRate();

        if(replyRateOfReply1 < replyRateOfReply2) {
            return 1;
        } else if(replyRateOfReply1 == replyRateOfReply2) {
            if(reply1.totalPrice > reply2.totalPrice) return 1;
            else if(reply1.totalPrice == reply2.totalPrice) return 0;
            else return -1;
        } else return -1;
    }
}
