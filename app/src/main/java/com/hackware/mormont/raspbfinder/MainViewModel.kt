package com.hackware.mormont.raspbfinder

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    companion object Constant{
        const val MAC_RE = "b8:27:eb:..:..:.."
    }
    private val mErrorMessage: MutableLiveData<Int> = MutableLiveData()
    val errorMessage: LiveData<Int>
        get() = mErrorMessage


    private fun onClickSearch(){

    }

}