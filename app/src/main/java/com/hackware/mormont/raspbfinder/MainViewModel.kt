package com.hackware.mormont.raspbfinder

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hackware.mormont.raspbfinder.model.Device
import com.hackware.mormont.raspbfinder.net.IpAddressScanner
import com.hackware.mormont.raspbfinder.net.NetManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(private var netManager: NetManager) : ViewModel() {
    companion object Constant {
        const val MAC_RE = "b8:27:eb:..:..:.."
        //TODO: add an ability to change the pattern, in case device's MAC was changed/spoofed
    }

    private val mRaspberryIpAddress: MutableLiveData<String> = MutableLiveData()
    val raspberryIpAddress: LiveData<String>
        get() = mRaspberryIpAddress

    private val mInfoMessage: MutableLiveData<Int> = MutableLiveData()
    val infoMessage: LiveData<Int>
        get() = mInfoMessage

    private val mErrorMessage: MutableLiveData<Int> = MutableLiveData()
    val errorMessage: LiveData<Int>
        get() = mErrorMessage

    init {
        mInfoMessage.value = R.string.tap_to_search_text
        mRaspberryIpAddress.value = ""
    }

    fun onClickSearch() {
        if (netManager.isNetworkConnected()) {
            mInfoMessage.value = R.string.search_in_progress
            viewModelScope.launch {
                val deviceList = withContext(Dispatchers.IO) {
                    IpAddressScanner().startPingService(netManager.getWifiManager())
                }
                searchRaspberryPi(deviceList)
            }
        } else {
            mErrorMessage.value = R.string.network_not_available
        }
    }

    private fun searchRaspberryPi(devices: ArrayList<Device>) {
        for (it in devices) {
            if (it.mac.matches(MAC_RE.toRegex())) {
                mInfoMessage.value = R.string.raspberry_ip
                mRaspberryIpAddress.value = it.ip
                break
            } else {
                mInfoMessage.value = R.string.not_found
            }
        }
    }
}