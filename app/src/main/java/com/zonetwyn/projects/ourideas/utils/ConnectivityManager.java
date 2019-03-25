package com.zonetwyn.projects.ourideas.utils;

import android.content.Context;
import android.net.NetworkInfo;

public class ConnectivityManager {

    public static boolean checkInternetConnection(Context context) {
        boolean isConnected = false;

        android.net.ConnectivityManager connectivityManager = (android.net.ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()) {
            isConnected = true;
        }

        return isConnected;
    }
}
