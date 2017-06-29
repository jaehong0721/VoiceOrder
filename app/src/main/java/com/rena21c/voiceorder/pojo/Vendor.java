package com.rena21c.voiceorder.pojo;


public class Vendor {
    public String name;
    public String phoneNumber;
    public String address;
    public String items;

    public Vendor() {}

    public Vendor(String name, String phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    @Override public String toString() {
        return "Vendor{" +
                "name='" + name + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", address='" + address + '\'' +
                ", items='" + items + '\'' +
                '}';
    }
}
