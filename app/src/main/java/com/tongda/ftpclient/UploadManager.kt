package com.tongda.ftpclient

import com.tongda.adv3.net.ftp.bean.FileInfoItem
import com.tongda.commonutil.L
import org.jetbrains.anko.doAsync

/**
* Created by chang on 2017-05-24.
* Mail:changfeng1050@hotmail.com
*/
class UploadManager : FtpManager.FtpListener {
    companion object {
        val TAG: String = L.makeLogTag(UploadManager::class.java)
    }

    var ftpAddress: String = ""
    var ftpPort: Int = 0
    var ftpUserName = ""
    var ftpPassword = ""

    private var ftpManager = FtpManager()

    private lateinit var fileList: List<FileInfoItem>

    /**
     * 当前已经上传大小
     */
    var current: Long = 0
    /**
     * 总大小
     */
    var total: Long = 0

    var listener: UploadListener? = null

    private lateinit var ftpDir: String
    var currentIndex = 0
    private var currentFileInfo: FileInfoItem? = null


    fun startUploadTask(dir: String, fileList: List<FileInfoItem>, listener: UploadListener) {
        this.listener = listener
        ftpDir = dir
        this.fileList = fileList

        ftpManager.hostAddress = ftpAddress
        ftpManager.port = ftpPort
        ftpManager.username = ftpUserName
        ftpManager.password = ftpPassword
        onUploadTaskInit()
        total = fileList.fold(0L) { t, n ->
            t + n.size
        }
        doAsync {
            uploadNext()
        }

    }

    fun finishUploadTask() {
        ftpManager.logout()
    }

    private fun onUploadTaskInit() {
        L.i(TAG, "onUploadTaskInit()")
        currentIndex = 0
        currentFileInfo = null
        current = 0
        total = 0
    }

    private fun uploadNext() {
        val fileInfo = fileList[currentIndex]
        currentFileInfo = fileInfo
        ftpManager.upLoadFile(ftpDir, currentFileInfo!!.path, this)
    }

    private fun onUploaded(fileInfo: FileInfoItem) {
        currentIndex++
        if (currentIndex >= fileList.count()) {
            L.i(TAG, "onUploaded finish")
            listener?.onCompletion(total)
            return
        }
        uploadNext()
    }


    override fun started() {
        L.i(TAG, "started()")
        if (currentIndex == 0) {
            listener?.onStarted(current, total)
        }
    }

    override fun transferred(p0: Int) {
        L.i(TAG, "transferred() $p0")
        current += p0
        listener?.onTransferred(current, total)
    }

    override fun completed() {
        L.i(TAG, "completed()")
        onUploaded(currentFileInfo!!)
    }

    override fun aborted() {
        L.i(TAG, "aborted()")
        listener?.onAborted(current, total, "currentFileInfo!!.path", "")
    }

    override fun previous(localSize: Long) {
        L.i(TAG, "previous() $localSize")
        current += localSize
    }

    override fun failed(path: String, msg: String) {
        L.i(TAG, "failed() $path $msg")
        listener?.onFailed(current, total, path, msg)
    }

    interface UploadListener {
        /**
         * 开始
         */
        fun onStarted(current: Long, total: Long)

        /**
         * 正在传输
         */
        fun onTransferred(current: Long, total: Long)

        /**
         * 完成
         */
        fun onCompletion(total: Long)

        /**
         * 中断
         */
        fun onAborted(current: Long, total: Long, path: String, msg: String)

        /**
         * 失败
         */
        fun onFailed(current: Long, total: Long, path: String, msg: String)
    }
}