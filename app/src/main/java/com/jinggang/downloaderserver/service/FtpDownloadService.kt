package com.jinggang.downloaderserver.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.jinggang.downloaderserver.util.sleepIgnoreInterrupt

class FtpDownloadService : Service(), Runnable {

    companion object {
        private const val TAG: String = "MonitorService"

        const val ACTION_POWER_ON = "com.tongda.action.POWER_ON"
        const val ACTION_POWER_OFF = "com.tongda.action.POWER_OFF"

        const val STATE_ON = 0 // 上电
        const val STATE_OFF = 1 // 掉电
    }

    @Volatile
    private var shouldExit = false

    private var serverThread: Thread? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        shouldExit = false

        // The previous server thread may still be cleaning up, wait for it to finish
        var attempts = 10
        while (serverThread != null) {
            Log.w(TAG, "Won`t start, server thread exists")
            if (attempts > 0) {
                attempts--
                sleepIgnoreInterrupt(1000)
            } else {
                Log.w(TAG, "Server thread already exists")
                return START_STICKY
            }
        }
        Log.d(TAG, "Creating server thread")
        serverThread = Thread(this)
        serverThread!!.start()
        return START_STICKY
    }

    private fun isRunning(): Boolean {
        // return true if and only if a server Thread is running
        if (serverThread == null) {
            Log.d(TAG, "Server is not running (null serverThread)")
            return false
        }
        if (!serverThread!!.isAlive) {
            Log.d(TAG, "serverThread non-null but !isAlive()")
        } else {
            Log.d(TAG, "Server is alive")
        }
        return true
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        Log.i(TAG, "onDestroy() Stopping server")

        shouldExit = true

        if (serverThread == null) {
            Log.w(TAG, "Stopping with null serverThread")
            return
        }

        serverThread!!.interrupt()
        try {
            serverThread!!.join(10000)
        } catch (e: InterruptedException) {

        }
        if (serverThread!!.isAlive) {
            Log.w(TAG, "Server thread failed to exit")
        } else {
            Log.d(TAG, "serverThread join() end ok")
            serverThread = null
        }
    }

    override fun run() {
        while (!shouldExit) {

            Thread.sleep(1000L)
        }
    }
}
