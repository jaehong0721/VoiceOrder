package com.rena21c.voiceorder.model;


import java.io.Serializable;

public class OrderItem implements Serializable{

    public String name;
    public String count;

    public OrderItem() {}

    public OrderItem(String name, String count) {
        this.name = name;
        this.count = count;
    }

}
