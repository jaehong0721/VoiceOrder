package com.rena21c.voiceorder.model;



public class VendorInfo {
    public String vendorName;
    public String ownerName;
    public String phoneNumber;
    public String address;

    public VendorInfo() {}

    public VendorInfo(String vendorName, String phoneNumber) {
        this.vendorName = vendorName;
        this.phoneNumber = phoneNumber;
    }
}
