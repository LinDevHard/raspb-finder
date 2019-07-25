package com.lindevhard.android.raspfinder

import android.content.ClipData
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
import com.lindevhard.android.raspfinder.databinding.ActivityFullscreenBinding
import com.lindevhard.android.raspfinder.net.NetManager
import com.lindevhard.android.raspfinder.utils.enableFullscreenMode
import com.lindevhard.android.raspfinder.utils.getClipboardManager
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

        //Coping IP in clipboard
        rasp_ip.setOnClickListener {
            copyIp()
        }

        binding.executePendingBindings()
    }

    private fun copyIp() {
        val clipboardManager = application.getClipboardManager()
        val clip: ClipData = ClipData.newPlainText("IP", rasp_ip.text.toString())
        clipboardManager.primaryClip = clip
        showToast(R.string.info_message_clipboard)
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
