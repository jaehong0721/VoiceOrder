package com.rena21c.voiceorder;

import android.app.Application;
import android.content.Context;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.rena21c.voiceorder.etc.AppPreferenceManager;
import com.rena21c.voiceorder.etc.RecordedFileManager;
import com.rena21c.voiceorder.firebase.AnalyticsEventManager;
import com.rena21c.voiceorder.network.ConnectivityIntercepter;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class App extends Application {

    private Retrofit retrofit;

    public static App getApplication(Context context) {
        return (App) context;
    }

    private AppPreferenceManager appPreferenceManager;

    private RecordedFileManager recordedFileManager;

    private AnalyticsEventManager eventManager;

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

        recordedFileManager = new RecordedFileManager(getFilesDir());
    }

    public AppPreferenceManager getPreferenceManager() {
        return appPreferenceManager;
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }

    public RecordedFileManager getRecordedFileManager() { return recordedFileManager; }

    public AnalyticsEventManager getEventManager() {
        if(eventManager == null)
            eventManager = new AnalyticsEventManager(FirebaseAnalytics.getInstance(this), appPreferenceManager.getPhoneNumber());

        return eventManager;
    }
}
