package com.rena21c.voiceorder.etc;


import com.rena21c.voiceorder.model.DisplayedMyPartner;

import java.util.Comparator;

public class TimeSortComparator implements Comparator<DisplayedMyPartner> {

    @Override public int compare(DisplayedMyPartner o1, DisplayedMyPartner o2) {
        long timeMillis1 = o1.callTime;
        long timeMillis2 = o2.callTime;

        if(timeMillis1 < timeMillis2) return 1;
        else if(timeMillis1 == timeMillis2) return 0;
        else return -1;
    }
}
