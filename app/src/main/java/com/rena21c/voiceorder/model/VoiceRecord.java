package com.rena21c.voiceorder.model;

import java.io.Serializable;
import java.util.ArrayList;

public class VoiceRecord implements Serializable{

    public ArrayList<OrderItem> orderItems;
    public boolean accepted;

    public VoiceRecord() {}

    @Override
    public String toString() {
        return "VoiceRecord{" +
                "orderItems=" + orderItems +
                '}';
    }
}