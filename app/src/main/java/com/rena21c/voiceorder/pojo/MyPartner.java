package com.rena21c.voiceorder.pojo;


import java.util.ArrayList;
import java.util.HashMap;

public class MyPartner {
    public ArrayList<HashMap<String, String>> items;
    public String name;

    public MyPartner() {}

    public MyPartner(String name) {
        this.name = name;
    }
}
