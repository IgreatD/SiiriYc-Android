package com.siiri.yc.utils

import java.io.File
import java.math.BigInteger
import java.security.MessageDigest

object MD5Util {

    fun fileToMD5(file: File): String {
        val digest = MessageDigest.getInstance("md5")
        file.forEachBlock { buffer, bytesRead ->
            digest.update(buffer, 0, bytesRead)
        }
        return BigInteger(1, digest.digest()).toString(16)
    }

}