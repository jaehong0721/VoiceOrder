package com.rena21c.voiceorder.etc;


import com.rena21c.voiceorder.model.Contact;

import java.util.Comparator;

public class NameAscComparator implements Comparator<Contact> {

    @Override
    public int compare(Contact arg0, Contact arg1) {
        return arg0.name.compareTo(arg1.name);
    }

}
