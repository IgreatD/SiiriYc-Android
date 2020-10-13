package com.siiri.yc.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.blankj.utilcode.util.FileUtils
import java.io.File
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket
import java.net.URL

/**
 * @author: dinglei
 * @date: 2020/9/25 18:03
 */
object CommonUtils {

    fun updateFileLibrary(context: Context, file: File) {
        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        val contentUri: Uri = Uri.fromFile(file)
        mediaScanIntent.data = contentUri
        context.sendBroadcast(mediaScanIntent)
    }

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