package com.lindevhard.android.raspfinder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lindevhard.android.raspfinder.net.NetManager


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