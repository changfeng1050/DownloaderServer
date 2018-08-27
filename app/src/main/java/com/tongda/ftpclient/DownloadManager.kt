package com.tongda.ftpclient

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.tongda.commonutil.FileUtil
import com.tongda.ftpclient.bean.FileInfo
import com.tongda.commonutil.FileUtils
import com.tongda.commonutil.L
import com.tongda.commonutil.MD5Utils
import org.jetbrains.anko.doAsync
import java.io.File
import java.util.concurrent.Executor
import java.util.regex.Pattern

/**
 * Created by chang on 2017-05-18.
 * Mail:changfeng1050@hotmail.com
 */
class DownloadManager(val appExecutor: Executor) : FtpManager.FtpListener {

    companion object {
        val TAG: String = L.makeLogTag(DownloadManager::class.java)
        private const val FIle_LIST_NAME = "fileinfo.json"

        private const val DOWNLOAD_STATE_NON = 0
        private const val DOWNLOAD_STATE_FILE_INFO = 1
        private const val DOWNLOAD_STATE_FILE = 2

        val FILE_NAME_PATTERN: Pattern = Pattern.compile("^[0-9|a-z|A-Z]{32}") // 使用MD5值命令的文件名
    }

    private val gson: Gson = GsonBuilder()
            .serializeNulls()
            .create()

    var ftpAddress: String = ""
    var ftpPort: Int = 0
    var ftpUserName = ""
    var ftpPassword = ""

    var ftpManager = FtpManager()


    private var fileInfos: List<DownloadFileInfo>? = null

    private var currentTempFileInfoPath = ""

    /**
     * 下载文件类型
     */
    private var fileType: Int? = null

    private var downloadState = DOWNLOAD_STATE_NON

    /**
     * 当前已经下载大小
     */
    var current: Long = 0

    /**
     * 总大小
     */
    var total: Long = 0

    var listener: DownLoadListener? = null

    lateinit var tempLocalDir: String

    /**
     * 获取下载的文件的原MD5值
     */
    private fun getDownloadedFileOriginMd5(): String? {
        return if (currentFileInfo == null) {
            null
        } else {
            currentFileInfo!!.md5
        }
    }

    /**
     * 计算下下载的文件的MD5值
     */
    private fun getDownloadedFileMd5(): String? {
        return if (currentFileInfo == null) {
            null
        } else {
            MD5Utils.getMd5String(currentLocalPath)
        }
    }


    /**
     * 开始下载任务
     */
    fun startDownloadTask(dir: String, localDir: String, fileType: Int, listener: DownLoadListener) {
        this.listener = listener
        tempLocalDir = localDir
        val ftpFilePath = dir + File.separator + FIle_LIST_NAME
        this.fileType = fileType
        val tempFilePath = localDir + File.separator + FIle_LIST_NAME
        ftpManager.hostAddress = ftpAddress
        ftpManager.port = ftpPort
        ftpManager.username = ftpUserName
        ftpManager.password = ftpPassword
        onDownLoadTaskInit()
        currentTempFileInfoPath = tempFilePath
        doAsync {
            ftpManager.downloadFile(ftpFilePath, tempFilePath, 0L, this@DownloadManager)
        }
    }

    fun finishDownloadTask() {
        ftpManager.logout()
    }

    /**
     * 下载任务初始化
     */
    private fun onDownLoadTaskInit() {
        L.i(TAG, "onDownLoadTaskInit()")
        current = 0
        total = 1
        currentIndex = 0
        downloadState = DOWNLOAD_STATE_FILE_INFO
    }

    /**
     * 本地文件大小
     */
    override fun previous(localSize: Long) {
        current += localSize
    }


    override fun started() {
        L.i(TAG, "started()")
        when (downloadState) {
            DOWNLOAD_STATE_FILE -> {
                if (currentIndex == 0) {
                    listener?.onStarted(current, total)
                }
            }
        }
    }

    override fun transferred(current: Int) {
        this.current += current
        when (downloadState) {
            DOWNLOAD_STATE_FILE -> {
                listener?.onTransferred(this.current, total)
            }
        }
    }

    override fun completed() {
        L.i(TAG, "completed()")

        when (downloadState) {
            DOWNLOAD_STATE_NON -> {

            }
            DOWNLOAD_STATE_FILE_INFO -> {
                try {
                    val fileInfo = gson.fromJson(FileUtils.getStringFromFile(currentTempFileInfoPath), FileInfo::class.java)
                    val l = mutableListOf<DownloadFileInfo>()

                    val content = FileUtils.getStringFromFile(currentTempFileInfoPath)
                    L.i(TAG, "fileinfo.json content:$content")

                    fileInfo.files.forEach {
                        val dir = it.dir
                        l.addAll(it.items.map { DownloadFileInfo(path = dir + File.separator + it.name, size = it.size, md5 = it.md5) })
                    }
                    fileInfos = l
                    downloadState = DOWNLOAD_STATE_FILE

                    listener?.onFileNames(fileInfos!!.map { it.path.substringAfterLast(File.separator) })

                    total = l.fold(0L) { t, n ->
                        t + n.size
                    }
                    current = 0
                    downloadNext()
                } catch (e: Exception) {
                    L.e(TAG, "completed()", e)
                    listener?.onFailed(current, total, currentTempFileInfoPath, "解析文件协商表出错")
                }
            }

            DOWNLOAD_STATE_FILE -> {
                listener?.onCompletionOneFile(currentFileInfo!!.path.substringAfterLast(File.separator))

                val originMd5 = getDownloadedFileOriginMd5()?.toLowerCase()
                val md5 = getDownloadedFileMd5()?.toLowerCase()
                if (originMd5 == null || md5 == null || originMd5 != md5) {
                    val message = "文件校验错误 原文件MD5：$originMd5 下载的文件MD5：$md5 文件路径：${currentFileInfo!!.path}"
                    val file = File(currentLocalPath)
                    file.delete()
                    L.e(TAG, "isDownloadFileCorrect() $message")
                    listener?.onFailed(current, total, currentLocalPath!!, message)
                } else {
                    onDownloaded(currentFileInfo!!)
                }
            }
        }
    }

    override fun aborted() {
        L.i(TAG, "aborted()")
        listener?.onAborted(current, total, currentFileInfo?.path
                ?: "null", "FTP传输连接中断")
    }

    override fun failed(path: String, msg: String) {
        listener?.onFailed(current, total, currentFileInfo?.path ?: "null", msg)
    }

    private var currentIndex = 0
    private var currentFileInfo: DownloadFileInfo? = null
    private fun downloadNext() {
        appExecutor.execute {
            val fileInfo = fileInfos!![currentIndex]
            currentFileInfo = fileInfo
            executeDownload(fileInfo)
        }
    }

    /**
     * 当前下载的文件的本地路径
     */
    private var currentLocalPath: String? = null

    private fun executeDownload(fileInfo: DownloadFileInfo) {
        L.i(TAG, "executeDownloadFile() $fileInfo")
        val fileName = fileInfo.path.substringAfterLast(File.separator)
        val localPath = tempLocalDir + File.separator + fileName
        val localFile = File(localPath)

        var startAt = 0L

        if (localFile.exists() && localFile.isDirectory) {
            localFile.deleteRecursively()
        }


        val fileNameWithoutSuffix = fileName.substringBeforeLast('.')
        val isMd5FormatName = FILE_NAME_PATTERN.matcher(fileNameWithoutSuffix).matches()
        val needDownload: Boolean = if (!isMd5FormatName) {
            L.i(TAG, "executeDownload() 文件格式不是MD5值格式:$fileName")
            true
        } else if (localFile.exists()) {
            val localFileSize = FileUtil.getFileSize(localFile)

            if (localFileSize > fileInfo.size) {
                localFile.delete()
                true
            } else if (localFileSize == fileInfo.size) {
                val md5 = MD5Utils.getMd5String(localPath)
                val need = md5.toLowerCase() != fileInfo.md5.toLowerCase()
                if (need) {
                    L.i(TAG, "executeDownload() origin md5:${fileInfo.md5} calculate md5:$md5  delete file ${fileInfo.path}")
                    localFile.delete()
                }
                need

            } else {
                startAt = localFileSize
                true
            }

        } else {
            true
        }


        if (needDownload) {
            onStartDownload(localPath)
            ftpManager.downloadFile(fileInfo.path, localPath, startAt, this)
            return
        }
        listener?.onCompletionOneFile(fileInfo.path.substringAfterLast(File.separator))
        current += fileInfo.size
        listener?.onTransferred(current, total)
        onDownloaded(fileInfo)
    }

    /**
     * 文件下载开始
     */
    private fun onStartDownload(localPath: String) {
        currentLocalPath = localPath
    }

    private fun onDownloaded(fileInfo: DownloadFileInfo) {
        currentIndex++
        if (currentIndex >= fileInfos!!.count()) {
            L.i(TAG, "onDownload() finish")
            downloadState = DOWNLOAD_STATE_NON
            listener?.onCompletion(total)
            return
        }
        downloadNext()
    }


    data class DownloadFileInfo(val path: String, val size: Long, val md5: String)


    interface DownLoadListener {
        /**
         * 开始下载
         */
        fun onStarted(current: Long, total: Long)

        /**
         * 正在传输
         */
        fun onTransferred(current: Long, total: Long)

        /**
         * 下载完成
         */
        fun onCompletion(total: Long)

        /**
         * 下载中断
         */
        fun onAborted(current: Long, total: Long, path: String, msg: String)

        /**
         * 下载失败
         */
        fun onFailed(current: Long, total: Long, path: String, msg: String)

        /**
         * 获取到需要下载的文件名
         */
        fun onFileNames(names: List<String>)

        /**
         * 完成下载文件
         */
        fun onCompletionOneFile(fileName: String)
    }
}
