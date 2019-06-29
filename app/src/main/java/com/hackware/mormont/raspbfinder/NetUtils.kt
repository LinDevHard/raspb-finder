package com.hackware.mormont.raspbfinder

import android.content.Context
import android.content.Context.WIFI_SERVICE
import android.net.wifi.WifiManager
import android.os.AsyncTask
import android.util.Log
import com.hackware.mormont.raspbfinder.model.Device
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.FileReader
import java.io.IOException
import java.net.InetAddress

@Deprecated("This class is deprecated")
class NetService: AsyncTask<Context, String, ArrayList<Device>>() {
    override fun doInBackground(vararg params: Context): ArrayList<Device>? {
        return startPingService(params[0])
    }

    @Deprecated(" Not recommended to use method. ")
    private fun startPingService(cont: Context) : ArrayList<Device> {
        val context = cont.applicationContext
        val deviceInfoList = ArrayList<Device>()
            val mWifiManager = context.getSystemService(WIFI_SERVICE) as WifiManager
            val subnet: String = getSubnetAddress(mWifiManager.dhcpInfo.gateway)

            for (i in 1..254) {
                val host = "$subnet.$i"
                val timeout = 20
                if (InetAddress.getByName(host).isReachable(timeout)) {
                    val strMacAddress = getMacAddressFromIP(host)
                    Log.i(
                        "DeviceDiscovery",
                        "Reachable Host: $host and Mac : $strMacAddress is reachable!"
                    )

                    val localDeviceInfo = Device(host, strMacAddress)
                    deviceInfoList.add(localDeviceInfo)
                } else {
                    Log.e("DeviceDiscovery", "❌ Not Reachable Host: $host")
                }

            }
        return deviceInfoList
    }

    private fun getSubnetAddress(address: Int): String =  String.format(
            "%d.%d.%d",
            address and 0xff,
            address shr 8 and 0xff,
            address shr 16 and 0xff
        )

    private fun getMacAddressFromIP(ipFinding: String): String {

        Log.i("IPScanning", "Scan was started!")

        var bufferedReader: BufferedReader? = null
        try {
            bufferedReader = BufferedReader(FileReader("/proc/net/arp"))

            while (true) {
                val line = bufferedReader.readLine() ?: break
                val splitted = line.split(" +".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                if (splitted.size >= 4) {
                    val ip = splitted[0]
                    val mac = splitted[3]
                    if (mac.matches("..:..:..:..:..:..".toRegex())) {
                        if (ip.equals(ipFinding, ignoreCase = true)) {
                            return mac
                        }
                    }
                }
            }

        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                bufferedReader!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return "00:00:00:00"
    }
}