package com.rena21c.voiceorder.model;


public class Contact {
    public String phoneNumber;
    public String name;

    public boolean isChecked;

    public Contact(String phoneNumber, String name) {
        this.phoneNumber = phoneNumber;
        this.name = name;
    }
}
