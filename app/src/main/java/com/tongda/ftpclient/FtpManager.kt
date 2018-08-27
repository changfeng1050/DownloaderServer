package com.tongda.ftpclient

import com.tongda.commonutil.L
import org.apache.commons.net.ftp.FTP
import org.apache.commons.net.ftp.FTPClient
import org.apache.commons.net.ftp.FTPConnectionClosedException
import org.apache.commons.net.io.CopyStreamEvent
import org.apache.commons.net.io.CopyStreamListener
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.net.SocketException

/**
 * Created by chang on 2017-04-08.
 * Mail:changfeng1050@hotmail.com
 */
class FtpManager {
    companion object {
        val TAG: String = L.makeLogTag(FtpManager::class.java)
    }

    var hostAddress = ""
    var port = 50000
    var username = ""
    var password = ""

    private var client: FTPClient? = null

    fun login(username: String, password: String) {
        this.username = username
        this.password = password
        try {
            client!!.login(username, password)
        } catch (e: Exception) {
            L.e(TAG, "login()", e)
        }
    }


    /**
     * 下载文件
     */
    fun downloadFile(ftpFile: String, localFile: String, startAt: Long, listener: FtpListener) {
        L.i(TAG, "downloadFile() from:$ftpFile to $localFile")
        try {

            handlerLogin()
            val localDir = File(localFile.substringBeforeLast(File.separator))
            if (localDir.exists() && localDir.isFile) {
                localDir.delete()
            }

            if (!localDir.exists()) {
                localDir.mkdirs()
            }

            val local = File(localFile)
            if (local.exists() && local.isDirectory) {
                local.deleteRecursively()
            }
            listener.previous(startAt)

            client!!.restartOffset = startAt

            val outputStream = BufferedOutputStream(FileOutputStream(localFile, startAt > 0))
            listener.started()


            client!!.copyStreamListener = object : CopyStreamListener {
                override fun bytesTransferred(p0: CopyStreamEvent?) {
                }

                override fun bytesTransferred(p0: Long, p1: Int, p2: Long) {
                    listener.transferred(p1)
                }
            }

            val success = client!!.retrieveFile(ftpFile, outputStream)

            outputStream.close()
            if (success) {
                listener.completed()
            } else {
                listener.failed(ftpFile, "${TAG} downloadFile() retrieveFile not success:$ftpFile")
            }
        } catch (e: FTPConnectionClosedException) {
            L.e(TAG, "downloadFile()", e)
            listener.failed(ftpFile, "${TAG} downloadFile() ${e.message}")
        } catch (e: SocketException) {
            L.e(TAG, "downloadFile()", e)
            listener.failed(ftpFile, "${TAG} downloadFile() ${e.message}")
        } catch (e: Exception) {
            L.e(TAG, "downLoadFile()", e)
            listener.failed(ftpFile, "${TAG} downloadFile() ${e.message}")
        }
    }

    fun upLoadFile(dir: String, localFile: String, listener: FtpListener) {
        L.i(TAG, "uploadFile() $localFile")
        try {
            handlerLogin()
            val fileName = localFile.substringAfterLast(File.separator)
            listener.started()
            val remoteFile = dir + File.separator + fileName
            client!!.deleteFile(remoteFile)
            val inputStream = FileInputStream(localFile)
            val success = client!!.storeFile(remoteFile, inputStream)
            inputStream.close()

            if (success) {
                listener.completed()
            } else {
                listener.failed(localFile, "${TAG} uploadFile store file not success")
            }

        } catch (e: Exception) {
            L.e(TAG, "upLoadFile()", e)
            listener.failed(localFile, "${TAG} upLoadFile() dir:$dir  error:${e.message}")
        } finally {
        }
    }

    /**
     * 尝试登陆，如果已经登陆直接返回，没有登陆，则登陆
     */
    private fun handlerLogin() {

        try {
            client?.noop()
        } catch (e: Exception) {
            L.e(TAG, "handlerLogin() send noop to server", e)
            client = null
        }
        if (client == null) {
            createNewClient()
        }
    }


    private fun createNewClient() {
        logout()
        client = FTPClient()

        if (!client!!.isConnected) {
            client!!.connect(hostAddress, port)
        }

        client!!.login(username, password)

        client!!.enterLocalPassiveMode()
        client!!.setFileType(FTP.BINARY_FILE_TYPE)
    }


    /**
     * 退出FTP
     */
    fun logout() {
        try {
            client?.logout()
            client = null
        } catch (e: Exception) {
            L.e(TAG, "logout()", e)
        }
    }

    interface FtpListener {
        fun started()
        fun transferred(current: Int)
        fun completed()
        fun aborted()
        //        fun failed()
        fun previous(localSize: Long)

        fun failed(path: String, msg: String)
    }

}