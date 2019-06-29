package com.hackware.mormont.raspbfinder.net

import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager

class NetManager(applicationContext: Context) {
    private var context: Context = applicationContext

    fun getWifiManager(): WifiManager {
        return context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    }

    fun isNetworkConnected(): Boolean {
        val connectivityManager = context.applicationContext.getSystemService(
            Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }
}