package com.ilavista.minsksale.network;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * A wrapper for Android's Connectivity Manager.
 */
public class ConnectionManager {
    private final ConnectivityManager connectivityManager;

    public ConnectionManager(Context context) {
        connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public NetworkInfo getActiveNetworkInfo() {
        return connectivityManager.getActiveNetworkInfo();
    }

    public boolean isNetworkAvailable() {
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }
}
