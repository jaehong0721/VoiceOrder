package com.rena21c.voiceorder.model;


public class Partner {
    public String vendorName;
    public String vendorPhoneNumber;
    public long callTime;

    public Partner(String vendorPhoneNumber, String vendorName, long callTime) {
        this.vendorPhoneNumber = vendorPhoneNumber;
        this.vendorName = vendorName;
        this.callTime = callTime;
    }
}
