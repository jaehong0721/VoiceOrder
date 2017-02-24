package com.rena21c.voiceorder.network;


import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitSingleton {

    private static Retrofit INSTANCE = null;

    private RetrofitSingleton() {

    }

    public static Retrofit getInstance() {
        String url = "http://52.78.67.115:9000";
        if (INSTANCE == null) {
            INSTANCE = new Retrofit.Builder().baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            return INSTANCE;
        } else {
            return INSTANCE;
        }
    }
}
