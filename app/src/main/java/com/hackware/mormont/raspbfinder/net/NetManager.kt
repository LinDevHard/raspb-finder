package com.hackware.mormont.raspbfinder.net

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.wifi.WifiManager

class NetManager(applicationContext: Context) {
    private var context: Context = applicationContext

    fun getWifiManager(): WifiManager =
        context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

    fun isNetworkConnected(): Boolean {
        val connectivityManager = context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE)
        return if (connectivityManager is ConnectivityManager) {
            val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
            networkInfo?.isConnected ?: false
        } else false
    }
}