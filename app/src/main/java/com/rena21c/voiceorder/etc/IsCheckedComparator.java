package com.rena21c.voiceorder.etc;


import com.rena21c.voiceorder.model.Contact;

import java.util.Comparator;

public class IsCheckedComparator implements Comparator<Contact> {

    @Override public int compare(Contact o1, Contact o2) {
        boolean isChecked1 = o1.isChecked;
        boolean isChecked2 = o2.isChecked;

        if(isChecked1 == isChecked2) return 0;

        else {
            if (isChecked1) return -1;

            else return 1;
        }
    }
}
