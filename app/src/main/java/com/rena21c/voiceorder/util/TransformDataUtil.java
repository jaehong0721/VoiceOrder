package com.rena21c.voiceorder.util;


import java.util.List;

public class TransformDataUtil {

    public static String makeMajorItemsString(List<String> majorItems) {
        if(majorItems == null) return null;

        StringBuilder sb = new StringBuilder();
        for(String majorItem : majorItems) {
            sb.append(majorItem);
            sb.append(",");
        }
        sb.deleteCharAt(sb.length()-1);
        return sb.toString();
    }
}
