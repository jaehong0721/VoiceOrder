package com.rena21c.voiceorder;

import android.app.Application;
import android.content.Context;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class App extends Application {

    public static App getApplication(Context context) {
        return (App) context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/NanumSquareRegular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }
}
