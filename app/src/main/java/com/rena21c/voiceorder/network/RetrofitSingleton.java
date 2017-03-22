package com.rena21c.voiceorder.network;


import android.content.Context;

import okhttp3.OkHttpClient;
import okhttp3.internal.tls.OkHostnameVerifier;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitSingleton {

    private static Retrofit INSTANCE = null;

    private RetrofitSingleton() {

    }

    public static Retrofit getInstance(Context context) {

        String url = "http://52.78.67.115:9000";
        if (INSTANCE == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new ConnectivityIntercepter(context))
                    .build();
            INSTANCE = new Retrofit
                    .Builder()
                    .client(client)
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            return INSTANCE;
        } else {
            return INSTANCE;
        }
    }
}
