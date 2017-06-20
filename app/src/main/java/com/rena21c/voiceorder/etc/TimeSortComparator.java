package com.rena21c.voiceorder.etc;


import com.rena21c.voiceorder.model.Partner;

import java.util.Comparator;

public class TimeSortComparator implements Comparator<Partner> {

    @Override public int compare(Partner o1, Partner o2) {
        long timeMillis1 = o1.callTime;
        long timeMillis2 = o2.callTime;

        if(timeMillis1 < timeMillis2) return 1;
        else if(timeMillis1 == timeMillis2) return 0;
        else return -1;
    }
}
