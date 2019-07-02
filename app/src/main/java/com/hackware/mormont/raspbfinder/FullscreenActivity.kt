package com.hackware.mormont.raspbfinder

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.hackware.mormont.raspbfinder.databinding.ActivityFullscreenBinding
import com.hackware.mormont.raspbfinder.net.NetManager
import com.hackware.mormont.raspbfinder.utils.enableFullscreenMode

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

        viewModel.errorMessage.observe(this, Observer { errorMessage ->
            if (errorMessage != null) showError(errorMessage)
        })

        binding.executePendingBindings()
    }

    override fun onResume() {
        super.onResume()
        window.decorView.enableFullscreenMode()
    }

    private fun showError(error: Int) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show()
    }
}
