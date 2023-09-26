package com.mcal.dot.utils

import java.io.IOException
import java.net.URL

object NetHelper {
    fun isInternetAvailable(): Boolean {
        return try {
            val conn = URL("https://www.google.com").openConnection()
            conn.connectTimeout = 5000
            conn.connect()
            conn.getInputStream().close()
            true
        } catch (e: IOException) {
            false
        }
    }
}