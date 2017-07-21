package com.rena21c.voiceorder.model;


import com.rena21c.voiceorder.pojo.MyPartner;

public class DisplayedMyPartner extends MyPartner {
    public String phoneNumber;
    public long callTime;

    public DisplayedMyPartner() {}

    public DisplayedMyPartner(String name, String phoneNumber, long callTime) {
        super(name);
        this.phoneNumber = phoneNumber;
        this.callTime = callTime;
    }
}
