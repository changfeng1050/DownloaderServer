package com.jinggang.downloaderserver.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.os.IBinder
import android.os.SystemClock
import com.jinggang.downloaderserver.MainActivity
import com.jinggang.downloaderserver.bean.FtpData
import com.jinggang.downloaderserver.receiver.AlarmReceiver
import com.tongda.commonutil.AppExecutors
import com.tongda.commonutil.FileUtils
import com.tongda.commonutil.L
import com.tongda.commonutil.MD5Utils
import com.tongda.ftpclient.DownloadManager
import java.io.File
import android.content.pm.PackageInfo
import java.nio.file.Files.size
import android.content.pm.PackageManager
import org.jetbrains.anko.*


class LongRunningService : Service() {
    companion object {

        const val TAG: String = "LongRunningService"

        const val targetApp = "com.jingang.adfabuyun"

        /**
         * 传输进度步长
         */
        const val TRANSFER_PROGRESS_PERCENTAGE_STEP = 0.05 // 每隔 5% 上传一次进度
        const val TRANSFER_PROGRESS_SIZE_STEP = 1024 * 1024 // 每隔 1M 上传一次进度
        const val TRANSFER_PROGRESS_INTERVAL_STEP = 60 * 1000L // 每隔1分钟上传一次进度

        const val PREF_NAME = "tcp"
        const val KEY_LAST_REBOOT_TIME = "last_reboot_time"
        const val KEY_IS_AUTO_REBOOT = "is_auto_reboot"
        /**
         * 已经登陆过服务器，在开机后连接不会服务器的情况下，用于判断是否需要重启设备以便让以太网卡尝试连接到网络
         */
        const val KEY_EVER_LOGIN = "ever_login"
        /**
         * 上次登陆的服务器地址
         */
        const val KEY_LAST_LOGIN_TCP_ADDRESS = "last_login_tcp_address"
        /**
         * 上次登陆的服务器端口
         */
        const val KEY_LAST_LOGIN_TCP_PORT = "last_login_tcp_port"
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    @Volatile
    var isDownloading = false

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        L.i(TAG, "onStartCommand")
        if (!isDownloading) {
            doAsync {
                downloadApk()
            }
        }

        val interval = defaultSharedPreferences.getInt(MainActivity.KEY_FTP_FETCH_INTERVAL, 10)


        val manger = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val delay = interval * 60 * 1000
        val triggerTime = SystemClock.elapsedRealtime() + delay
        val i = Intent(this, AlarmReceiver::class.java)
        val pi = PendingIntent.getBroadcast(this, 0, i, 0)
        manger.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime, pi)

        return super.onStartCommand(intent, flags, startId)
    }

    private val appExecutors = AppExecutors()
    /**
     * 下载类
     */
    private val downloadManger = DownloadManager(appExecutors.networkIO())


    fun sendCallBackError(msg: String) {
        L.e(TAG, "sendCallbackError:$msg")
        runOnUiThread {
            longToast("错误信息 $msg")
        }
    }

    fun sendCallBackMessage(msg: String) {
        L.i(TAG, "sendCallBackMessage:$msg")
        runOnUiThread {
            toast("FTP信息 $msg")
        }
    }


    var tempDir = Environment.getExternalStorageDirectory().absolutePath + File.separator + "ftp_download"
    /**
     * 下载的文件的路径
     */
    private lateinit var downloadFilePaths: List<String>

    fun downloadApk() {
        File(tempDir).mkdirs()

        val dir = "app/apk"


//        val ftpData = FtpData("192.168.168.102", 9000, "changfeng", "123456")
//        val ftpData = FtpData("ftp.mcmsmp.com", 14147, "mcms_ftp_account", "Mcms_PW_1@3$")
        val ftpData = FtpData("120.24.234.199", 21, "mcms_ftp_account", "Mcms_PW_1@3$")
        L.i(TAG, "downloadApk $ftpData dir:$dir")

        downloadManger.ftpAddress = ftpData.address
        downloadManger.ftpPort = ftpData.port
        downloadManger.ftpUserName = ftpData.username
        downloadManger.ftpPassword = ftpData.password

        var lastProgress = 0L
        var lastProgressTime = System.currentTimeMillis()

        val funSendDownloadState = fun(stateCode: Int, current: Long, total: Long, info: String) {
            L.i(TAG, "downloadState stateCode:$stateCode $current/$total $info")
//            lastProgress = current
//            lastProgressTime = System.currentTimeMillis()
//            val frame = frameManager.packDownloadFileState(stateCode, current, total, info)
//            onNewSend(FrameManager.FRAME_SEND_DOWNLOAD_FILE_STATE_RESPONSE, frameManager.frameNo, frame)
        }
        // 检查参数
        if (dir.isBlank()) {
            val message = "下载文件参数不正确，路径为空"
            sendCallBackError(message)
            funSendDownloadState(FrameManager.DOWNLOAD_STATE_PARAM_INVALID, 0, 0xFFFF, message)
            return
        }


        val needSendTransferState = fun(current: Long, total: Long): Boolean {
            var result = if (current - lastProgress > TRANSFER_PROGRESS_SIZE_STEP) {
                true
            } else {
                val step = total * TRANSFER_PROGRESS_PERCENTAGE_STEP
                current - lastProgress > step
            }

            if (!result) {
                result = System.currentTimeMillis() - lastProgressTime > TRANSFER_PROGRESS_INTERVAL_STEP
            }

            return result
        }


        val downloadListener = object : DownloadManager.DownLoadListener {
            override fun onFileNames(names: List<String>) {
                val logMessage = "需要下载的文件，个数：${names.count()} 文件列表：${names.joinToString("|")}"
                L.i(TAG, "onRequestDownloadFile, onFileNames:$logMessage")
                sendCallBackMessage(logMessage)
                downloadFilePaths = names.map { tempDir + File.separator + it }
            }

            override fun onStarted(current: Long, total: Long) {
                val logMessage = "开始下载,本地已下载大小/总大小：$current/$total  ${FileUtils.byteToMB(current)}/${FileUtils.byteToMB(total)}"
                L.i(TAG, "onRequestDownloadFile, onStarted:$logMessage")
                sendCallBackMessage(logMessage)
                funSendDownloadState(FrameManager.DOWNLOAD_STATE_START, current, total, "")
            }

            override fun onTransferred(current: Long, total: Long) {
                if (needSendTransferState(current, total)) {
                    val message = "下载进度 $current / $total"
                    L.i(TAG, "onTransferred() $message")
                    sendCallBackMessage(message)
                    funSendDownloadState(FrameManager.DOWNLOAD_STATE_TRANSFERRING, current, total, "")
                }
            }

            override fun onCompletionOneFile(fileName: String) {
                val message = "下载完一个文件：$fileName"
                L.i(TAG, "onCompletionOneFile() $message")
                sendCallBackMessage(message)
            }

            override fun onCompletion(total: Long) {
                isDownloading = false
                try {
                    val message = "下载完成,总大小：$total  ${FileUtils.byteToMB(total)}"
                    L.i(TAG, "onRequestDownloadFile, onCompletion:$message")
                    sendCallBackMessage(message)
                    funSendDownloadState(FrameManager.DOWNLOAD_STATE_COMPLETE, total, total, "")
                    downloadManger.finishDownloadTask()
                    downloadFilePaths.filter { it.endsWith("apk") || it.endsWith("APK") }.forEach {
                        onApkDownloaded(it)
                    }

                } catch (e: Exception) {
                    val message = "下载完成，执行操作出现错误：${e.message}"
                    funSendDownloadState(FrameManager.DOWNLOAD_STATE_EXECUTE_UPDATE_ERROR, 0, 0xFF, message)
                    sendCallBackMessage(message)
                }
            }


            override fun onAborted(current: Long, total: Long, path: String, msg: String) {
                isDownloading = false
                val message = "下载文件中断，已下载大小/总大小:$current/$total  ${FileUtils.byteToMB(current)}/${FileUtils.byteToMB(total)} 文件：$path $msg"
                L.e(TAG, "onRequestDownloadFile, onAborted:$message")
                sendCallBackError(message)

                funSendDownloadState(FrameManager.DOWNLOAD_STATE_ABORT, current, total, "path:$path $msg")
                downloadManger.finishDownloadTask()
            }

            override fun onFailed(current: Long, total: Long, path: String, msg: String) {
                isDownloading = false
                val message = "下载文件失败，已下载大小/总大小:$current/$total  ${FileUtils.byteToMB(current)}/${FileUtils.byteToMB(total)} 文件：$path $msg"
                L.e(TAG, "onRequestDownloadFile, onFailed:$message")
                sendCallBackError(message)
                funSendDownloadState(FrameManager.DOWNLOAD_STATE_FAILED, current, total, "path:$path $msg")
                downloadManger.finishDownloadTask()
            }
        }
        downloadManger.startDownloadTask(dir, tempDir, FrameManager.FILE_TYPE_APK, downloadListener)

    }

    fun onApkDownloaded(path: String) {
        L.i(TAG, "onApkDownloaded path:$path")
        try {
            if (!isAvilible(this, targetApp)) {
                installApk(this, path)
                return
            }

            val md5 = MD5Utils.getMd5String(path)
            val apkMd5 = defaultSharedPreferences.getString("apk_md5", "")
            if (md5.toLowerCase() != apkMd5.toLowerCase()) {
                installApk(this, path)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun installApk(context: Context, path: String) {
        L.i(TAG, "installApk()$path")
        if (path.isEmpty()) {
            L.e(TAG, "installApk() file not found:$path")
            return
        }

        val file = File(path)
        if (file.exists()) {
            L.i(ContentValues.TAG, "installApk() file:$path")
            val intent = Intent(Intent.ACTION_VIEW)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.setDataAndType(Uri.fromFile(File(path)), "application/vnd.android.package-archive")
            context.startActivity(intent)
        }
    }

    /**
     * 判断相对应的APP是否存在
     *
     * @param context
     * @param packageName(包名)(若想判断QQ，则改为com.tencent.mobileqq，若想判断微信，则改为com.tencent.mm)
     * @return
     */
    fun isAvilible(context: Context, packageName: String): Boolean {
        val packageManager = context.packageManager

        //获取手机系统的所有APP包名，然后进行一一比较
        val pinfo = packageManager.getInstalledPackages(0)
        for (i in pinfo.indices) {
            if ((pinfo[i] as PackageInfo).packageName
                            .equals(packageName, ignoreCase = true))
                return true
        }
        return false
    }

}
