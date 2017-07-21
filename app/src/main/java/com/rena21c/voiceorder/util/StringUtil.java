package com.rena21c.voiceorder.util;


public class StringUtil {

    public static String removeSpecialLetter(String str){
        String match = "[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]";
        str =str.replaceAll(match, "");
        return str;
    }
}
