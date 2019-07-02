package com.hackware.mormont.raspbfinder.utils

import android.view.View

fun Int.getSubnetAddress(): String {
    return String.format(
        "%d.%d.%d",
        this and 0xff,
        this shr 8 and 0xff,
        this shr 16 and 0xff
    )
}

fun View.enableFullscreenMode() {
    systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_FULLSCREEN)
}