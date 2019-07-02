package com.hackware.mormont.raspbfinder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hackware.mormont.raspbfinder.net.NetManager


class ViewModelFactory(
    private val netManager: NetManager
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(MainViewModel::class.java) ->
                    MainViewModel(netManager)
                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}