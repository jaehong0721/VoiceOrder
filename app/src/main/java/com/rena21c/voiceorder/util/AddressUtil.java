package com.rena21c.voiceorder.util;


public class AddressUtil {
    public static String convertToSimpleAddress(String fullAddress) {
        String[] array = fullAddress.split(" ");
        return array.length < 2 ? array[0].substring(0,2) : array[0].substring(0,2) + " " + array[1];
    }
}
