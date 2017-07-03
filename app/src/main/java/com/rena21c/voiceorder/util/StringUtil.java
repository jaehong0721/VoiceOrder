package com.rena21c.voiceorder.util;


import android.util.Log;

public class StringUtil {

    public static String removeSpecialLetter(String str){
        String match = "[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]";
        str =str.replaceAll(match, "");
        Log.d("test",str);
        return str;
    }
}
