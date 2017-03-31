package com.rena21c.voiceorder;

import android.app.Application;
import android.content.Context;

import com.rena21c.voiceorder.model.Order;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class App extends Application {

    public static App getApplication(Context context) {
        return (App) context;
    }

    public ArrayList<Order> orders;

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    private void init() {

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/NanumSquareRegular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        orders = new ArrayList<>();
    }

    public static String makeTimeFromFileName(String fileName) {
        StringBuffer sb = new StringBuffer();
        String timeStamp = fileName.substring(16,26);
        for(int i =0; i<timeStamp.length(); i++) {
            if(i==2) {
                sb.append(".");
            }
            if(i==4) {
                sb.append("  ");
            }
            if(i==6) {
                sb.append(":");
            }
            if(i==8) {
                sb.append(":");
            }
            sb.append(timeStamp.charAt(i));
        }
        return sb.toString();
    }
}
