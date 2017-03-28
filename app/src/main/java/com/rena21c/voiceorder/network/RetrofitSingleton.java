package com.rena21c.voiceorder.network;


import android.content.Context;

import com.rena21c.voiceorder.R;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitSingleton {

    private static Retrofit INSTANCE = null;

    private RetrofitSingleton() {

    }

    public static Retrofit getInstance(Context context) {

        String url = context.getString(R.string.server_address);
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
