package com.rena21c.voiceorder.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class ConnectivityIntercepter implements Interceptor {

    private final Context context;

    public ConnectivityIntercepter(Context context) {
        this.context = context;
    }

    /**
     * 인터넷 연결 상태를 확인연결 되어 있지 않은 경우 예외를 던짐
     */
    @Override public Response intercept(Chain chain) throws IOException {
        if (!isConnectedToInternet()) {
            throw new NoConnectivityException();
        }
        Request.Builder builder = chain.request().newBuilder();
        return chain.proceed(builder.build());
    }

    private boolean isConnectedToInternet() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return (netInfo != null && netInfo.isConnected());
    }

}
