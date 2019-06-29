package com.hackware.mormont.raspbfinder.utils

fun Int.getSubnetAdress() :String{
    return String.format(
        "%d.%d.%d",
        this and 0xff,
        this shr 8 and 0xff,
        this shr 16 and 0xff)
}