package com.lindevhard.android.raspfinder.net

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.os.Build

class NetManager(applicationContext: Context) {
    private var context: Context = applicationContext

    fun getContext() = context

    fun getWifiManager(): WifiManager =
        context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

    fun isNetworkConnected(): Boolean {
        val connectivityManager =
            context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return if (Build.VERSION.SDK_INT < 23) {
            val networkInfo = connectivityManager.activeNetworkInfo
            networkInfo != null && networkInfo.isConnected
        } else {
            val networkInfo = connectivityManager.activeNetwork
            if (networkInfo != null) {
                val networkCapabilities = connectivityManager.getNetworkCapabilities(networkInfo)
                networkCapabilities != null && networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
            }
            false
        }
    }
}
