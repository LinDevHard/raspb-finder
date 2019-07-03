package com.hackware.mormont.raspbfinder

import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.annotation.AnimRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.hackware.mormont.raspbfinder.databinding.ActivityFullscreenBinding
import com.hackware.mormont.raspbfinder.net.NetManager
import com.hackware.mormont.raspbfinder.utils.enableFullscreenMode
import kotlinx.android.synthetic.main.activity_fullscreen.*

class FullscreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFullscreenBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.enableFullscreenMode()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_fullscreen)
        viewModel =
            ViewModelProviders.of(this, ViewModelFactory(NetManager(applicationContext))).get(MainViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        //observe the liveData
        viewModel.toastMessage.observe(this, Observer { toastMessage ->
            if (toastMessage != null) showToast(toastMessage)
        })
        viewModel.infoMessage.observe(this, Observer {
            showAnim(R.anim.fade_in, rasp_text)
        })
        viewModel.raspberryIpAddress.observe(this, Observer {
            showAnim(R.anim.slide_in_bottom, rasp_ip)
        })

        binding.executePendingBindings()
    }

    private fun showAnim(@AnimRes anim: Int, target: View) {
        val animation = AnimationUtils.loadAnimation(this, anim)
        target.startAnimation(animation)
    }

    private fun showToast(@StringRes text: Int) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }

    override fun onResume() {
        super.onResume()
        window.decorView.enableFullscreenMode()
    }
}
