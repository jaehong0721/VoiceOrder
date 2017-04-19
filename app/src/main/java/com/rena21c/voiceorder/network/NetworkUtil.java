package com.rena21c.voiceorder.network;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtil {

    public enum InternetConnection {
        NOT_CONNECTED, WIFI, ETC
    }

    public static InternetConnection getConnection(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if (!isConnected) return InternetConnection.NOT_CONNECTED;

        boolean isWiFi = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
        if (isWiFi) {
            return InternetConnection.WIFI;
        } else {
            return InternetConnection.ETC;
        }
    }

    public static boolean isInternetConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return (netInfo != null && netInfo.isConnected());
    }
}
