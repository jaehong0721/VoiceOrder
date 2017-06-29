package com.rena21c.voiceorder.model;


import com.rena21c.voiceorder.pojo.Vendor;

public class Partner extends Vendor {
    public long callTime;

    public Partner() {}

    public Partner(String name, String phoneNumber, long callTime) {
        super(name, phoneNumber);
        this.callTime = callTime;
    }
}
