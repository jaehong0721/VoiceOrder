package com.rena21c.voiceorder.util;


public class FileNameUtil {

    public static String getTimeFromFileName(String fileName) {
        return fileName.substring(12, 26);
    }

    public static String getDisplayTimeFromfileName(String fileName) {
        StringBuffer sb = new StringBuffer();
        String timeStamp = fileName.substring(4, 14);
        for (int i = 0; i < timeStamp.length(); i++) {
            if (i == 2) {
                sb.append(".");
            }
            if (i == 4) {
                sb.append("  ");
            }
            if (i == 6) {
                sb.append(":");
            }
            if (i == 8) {
                sb.append(":");
            }
            sb.append(timeStamp.charAt(i));
        }
        return sb.toString();
    }

    public static String makeFileName(String phoneNumber, long time) {
        String date = TimeUtil.convertMillisToDateFormat(time);
        return phoneNumber + "_" + date;
    }
}
