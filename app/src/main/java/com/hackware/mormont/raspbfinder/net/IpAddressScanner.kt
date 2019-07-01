package com.hackware.mormont.raspbfinder.net

import android.net.wifi.WifiManager
import android.util.Log
import com.hackware.mormont.raspbfinder.model.Device
import com.hackware.mormont.raspbfinder.utils.getSubnetAddress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException
import java.net.InetAddress
import java.util.regex.Matcher
import java.util.regex.Pattern


class IpAddressScanner {
    companion object {
        private const val TIMEOUT: Int = 50
        private const val BUF: Int = 8 * 1024
        private const val NON_MAC: String = "00:00:00:00"
        private const val MAC_RE = "^%s\\s+0x1\\s+0x2\\s+([:0-9a-fA-F]+)\\s+\\*\\s+\\w+$"
    }

    private lateinit var subnet: String
    private val deviceInfo: ArrayList<Device> = ArrayList<Device>()

    suspend fun startPingService(wm: WifiManager): ArrayList<Device> {
        Log.d("INFo", wm.dhcpInfo.toString())
        subnet = wm.dhcpInfo.gateway.getSubnetAddress()
        withContext(Dispatchers.IO) {
            for (i in 1..255 step 10) {
                if (i > 10) {
                    async { scanRangeIpAddress(i - 10, i) }
                }
            }

        }
        return deviceInfo
    }

    private fun scanRangeIpAddress(a: Int, b: Int): ArrayList<Device> {
        for (i in a..b) {
            val host = "$subnet.$i"
            if (InetAddress.getByName(host).isReachable(TIMEOUT)) {
                val strMacAddress = getMacAddressFromIP(host)
                Log.i(
                    "DeviceDiscovery",
                    "Reachable Host: $host and Mac : $strMacAddress is reachable!"
                )
                val localDeviceInfo = Device(host, strMacAddress)
                deviceInfo.add(localDeviceInfo)
            } else {
                Log.e("DeviceDiscovery", "❌ Not Reachable Host: $host")
            }
        }
        return deviceInfo
    }

    private fun getMacAddressFromIP(ip: String): String {
        var bufferedReader: BufferedReader? = null
        var hw = NON_MAC
        try {
            val paten: String = String.format(MAC_RE, ip.replace(".", "\\."))
            val pattern: Pattern = Pattern.compile(paten)
            bufferedReader = BufferedReader(FileReader("/proc/net/arp"), BUF)
            var matcher: Matcher
            while (true) {
                val line = bufferedReader.readLine() ?: break
                matcher = pattern.matcher(line)
                if (matcher.matches()) {
                    hw = matcher.group(1)
                    break
                }
            }
        } catch (e: IOException) {
            return hw
        } finally {
            try {
                bufferedReader?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return hw
    }
}