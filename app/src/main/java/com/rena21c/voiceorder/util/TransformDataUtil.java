package com.rena21c.voiceorder.util;


import com.rena21c.voiceorder.model.RequestedEstimateItem;

import java.util.ArrayList;
import java.util.Arrays;
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

    public static List<String> makeMajorItemsList(String majorItems) {
        if(majorItems == null || majorItems.equals("")) return null;

        String[] splitAreas = majorItems.split(",");
        List<String> areaList = new ArrayList<>(Arrays.asList(splitAreas));
        return areaList;
    }

    public static String makeRequestedEstimateItemsInLine(ArrayList<RequestedEstimateItem> items) {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i<items.size(); i++) {
            sb.append(items.get(i).itemName);
            sb.append(" ");
            sb.append(items.get(i).itemNum);
            sb.append(",");
        }
        sb.deleteCharAt(sb.length()-1);
        return sb.toString();
    }
}
