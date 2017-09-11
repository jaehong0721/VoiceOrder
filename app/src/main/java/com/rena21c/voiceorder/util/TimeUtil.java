package com.rena21c.voiceorder.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {

    private static final long SEC = 1000;
    private static final long MIN = 60 * SEC;
    private static final long HOUR = 60 * MIN;
    private static final long DAY = 24 * HOUR;
    private static final long WEEK = 7 * DAY;

    public static String convertMillisToElapsedTime(long current, long target) {

        if ((int)Math.log10(target)+1 != 13 && (int)Math.log10(current)+1 != 13) {
            throw new RuntimeException("밀리세컨드 단위가 아닙니다");
        }

        long elapsedTimeInMillis = current - target;

        if (elapsedTimeInMillis < MIN) {
            long seconds = elapsedTimeInMillis / SEC;
            return seconds == 0 ? "방금전" : seconds + "초전";
        } else if (elapsedTimeInMillis < HOUR) {
            long minutes = elapsedTimeInMillis / MIN;
            return minutes + "분전";
        } else if (elapsedTimeInMillis < DAY) {
            long hours = elapsedTimeInMillis / HOUR;
            return hours + "시간전";
        } else {
            long day = elapsedTimeInMillis / DAY;
            return day + "일전";
        }

    }

    public static String convertMillisToDateFormat(long millis) {
        SimpleDateFormat dayTime = new SimpleDateFormat("yyyyMMddHHmmss");
        return dayTime.format(new Date(millis));
    }

    public static long convertStringToMillis(String time) {
        SimpleDateFormat dayTime = new SimpleDateFormat("yyyyMMddHHmmss");
        long millis;
        try {
            millis = dayTime.parse(time).getTime();
        } catch (ParseException e) {
            millis = -1;
            e.printStackTrace();
        }
        return millis;
    }
    public static boolean isOverDueDate(long current, long target) {
        return current > target + 2 * WEEK;
    }
}
