package com.siiri.yc.utils

import android.util.Patterns
import android.webkit.URLUtil
import java.lang.Exception
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket
import java.net.URL

/**
 * @author: dinglei
 * @date: 2020/9/25 18:03
 */
object CommonUtils {

    fun isIpValid(urlString: String, timeout: Int): Boolean {
        val socket = Socket()
        try {
            val url = URL(urlString)
            val host = url.host
            val port = url.port
            val inetSocketAddress = InetSocketAddress(InetAddress.getByName(host), port)
            socket.connect(inetSocketAddress, timeout)
            return socket.isConnected
        } catch (e: Exception) {

        } finally {
            try {
                socket.close()
            } catch (e: Exception) {

            }
        }
        return false
    }

}