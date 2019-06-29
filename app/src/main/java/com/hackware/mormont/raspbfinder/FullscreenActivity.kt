package com.hackware.mormont.raspbfinder

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.hackware.mormont.raspbfinder.databinding.ActivityFullscreenBinding
import com.hackware.mormont.raspbfinder.net.NetManager
import kotlinx.android.synthetic.main.activity_fullscreen.*


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class FullscreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFullscreenBinding
    private lateinit var viewModel: MainViewModel
    private val mHideHandler = Handler()


    private val mHidePart2Runnable = Runnable {
        supportActionBar?.hide()
        rasp_ip.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LOW_PROFILE or
                    View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_fullscreen)
        viewModel =  ViewModelProviders.of(this, ViewModelFactory(NetManager(applicationContext))).get(MainViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.errorMessage.observe(this, Observer {
            errorMessage -> if (errorMessage != null) showError(errorMessage)
        })

        mHideHandler.post(mHidePart2Runnable)
        binding.executePendingBindings()
    }

    override fun onResume() {
        super.onResume()
        mHideHandler.post(mHidePart2Runnable)
    }

    fun getRaspAddress(v: View){
        val cm = application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val mWifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI)

        if (mWifi.isConnected) {
            rasp_text.setText(R.string.search_in_progress)
            val deviceList = with(NetService()){
                execute(applicationContext)
                get()
            }
            var isMatch = false

            for (it in deviceList) {
                if (it.mac.matches("b8:27:eb:..:..:..".toRegex())) {
                    rasp_ip.visibility = View.VISIBLE
                    rasp_text.setText(R.string.RaspberryIpInfo)
                    rasp_ip.text = it.host

                    isMatch = true
                    break
                }
            }
            if (!isMatch){
                rasp_ip.visibility = View.VISIBLE
                rasp_ip.setText(R.string.not_found)
            }
        } else {
            Toast.makeText(this,"error", Toast.LENGTH_LONG).show()
            }
        }

    private fun showError(error: Int){
        Toast.makeText(this, error, Toast.LENGTH_LONG).show()
    }
}
