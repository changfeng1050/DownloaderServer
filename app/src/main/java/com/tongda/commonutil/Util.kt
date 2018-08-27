package com.tongda.commonutil

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import java.io.File
import java.net.NetworkInterface
import java.net.SocketException
import java.nio.charset.Charset


/**
 * Created by chang on 2017-09-02.
 */

fun getLocalIpAddress(): String? {
    try {
        val en = NetworkInterface
                .getNetworkInterfaces()
        while (en.hasMoreElements()) {
            val intf = en.nextElement()
            val enumIpAddr = intf
                    .inetAddresses
            while (enumIpAddr.hasMoreElements()) {
                val inetAddress = enumIpAddr.nextElement()
                if (!inetAddress.isLoopbackAddress && !inetAddress.isLinkLocalAddress) {
                    return inetAddress.hostAddress
                }
            }
        }
    } catch (ex: SocketException) {
        Log.e(TAG, "WifiPreference IpAddress", ex)
    }

    return null
}


fun checkUpdateApp(context: Context, apkPath: String): Boolean {
    if (!File(apkPath).exists()) {
        return false
    }
    val md5 = MD5Utils.getMd5String(apkPath)
    val selfMd5 = MD5Utils.getMd5String(context.packageResourcePath)
    if (md5 == selfMd5) {
        return false
    }
    return true
}

fun checkDir(dir: String) {
    val file = File(dir)
    if (file.isFile) {
        file.delete()
    }

    if (!file.exists()) {
        file.mkdirs()
        return
    }
}

fun String.getDir(): String = this.substringBeforeLast(File.separator)

fun String.getName(): String = this.substringAfterLast(File.separator)

fun saveFile(filePath: String, text: String) {
    val file = File(filePath)
    if (file.exists()) {
        file.deleteRecursively()
    }
    val parentDir = File(filePath.getDir())
    parentDir.mkdirs()
    file.writeText(text)
}

fun readFile(filePath: String, charset: Charset = Charsets.UTF_8): String {
    return try {
        File(filePath).readText(charset)
    } catch (e: Exception) {
        ""
    }
}



