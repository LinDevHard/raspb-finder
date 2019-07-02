package com.hackware.mormont.raspbfinder

import android.content.ClipData
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hackware.mormont.raspbfinder.model.Device
import com.hackware.mormont.raspbfinder.net.NetManager
import com.hackware.mormont.raspbfinder.net.NetworkDiscovery
import com.hackware.mormont.raspbfinder.utils.getClipboardManager
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

    private val mToastMessage: MutableLiveData<Int> = MutableLiveData()
    val toastMessage: LiveData<Int>
        get() = mToastMessage

    init {
        mInfoMessage.value = R.string.info_message_search
        mRaspberryIpAddress.value = ""
    }

    fun onClickSearch() {
        if (netManager.isNetworkConnected()) {
            mInfoMessage.value = R.string.info_message_search_in_progress
            viewModelScope.launch {
                val deviceList = withContext(Dispatchers.IO) {
                    NetworkDiscovery().startPingService(netManager.getWifiManager())
                }
                searchRaspberryPi(deviceList)
            }
        } else {
            mToastMessage.value = R.string.error_message_network_not_available
        }
    }

    private fun searchRaspberryPi(devices: ArrayList<Device>) {
        for (it in devices) {
            if (it.mac.matches(MAC_RE.toRegex())) {
                mInfoMessage.value = R.string.info_message_raspberry_ip
                mRaspberryIpAddress.value = it.ip
                break
            } else {
                mInfoMessage.value = R.string.error_message_not_found
            }
        }
    }

    //Violates the viewModel concept
    fun copyIpToClipboard() {
        val clipboardManager = netManager.getContext().getClipboardManager()
        val clip: ClipData = ClipData.newPlainText("IP", mRaspberryIpAddress.value)
        clipboardManager.primaryClip = clip
        mToastMessage.value = R.string.info_message_clipboard
    }
}