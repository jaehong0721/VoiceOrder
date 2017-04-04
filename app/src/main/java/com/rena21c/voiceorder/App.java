package com.rena21c.voiceorder;

import android.app.Application;
import android.content.Context;

import com.rena21c.voiceorder.etc.AppPreferenceManager;
import com.rena21c.voiceorder.model.Order;
import com.rena21c.voiceorder.network.ConnectivityIntercepter;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class App extends Application {

    private Retrofit retrofit;

    public static App getApplication(Context context) {
        return (App) context;
    }

    public ArrayList<Order> orders;
    private AppPreferenceManager appPreferenceManager;

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

        appPreferenceManager = new AppPreferenceManager(this);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new ConnectivityIntercepter(this))
                .build();
        String url = getString(R.string.server_address);
        retrofit = new Retrofit
                .Builder()
                .client(client)
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static String makeTimeFromFileName(String fileName) {
        StringBuffer sb = new StringBuffer();
        String timeStamp = fileName.substring(16, 26);
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

    public AppPreferenceManager getPreferenceManager() {
        return appPreferenceManager;
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }
}
