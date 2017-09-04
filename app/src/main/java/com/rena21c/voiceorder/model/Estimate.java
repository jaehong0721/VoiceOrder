package com.rena21c.voiceorder.model;


import java.util.ArrayList;

public class Estimate {
    public String restaurantAddress;
    public String restaurantName;
    public long timeMillis;
    public ArrayList<RequestedEstimateItem> items;
}
