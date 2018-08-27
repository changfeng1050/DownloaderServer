package com.tongda.commonutil

import android.util.Log
import java.io.*

/**
 * Created by chang on 2016-09-24.
 */
object ShellUtil {


    private val mRunnableShutdown = Runnable { shutdown() }


    private val RTC_DEV = "/sys/class/rtc/rtc0/wakealarm"

    private fun delayPowerOn(delayMillis: Long): Boolean {
        if (delayMillis <= 0)
            return false

        Log.d("#DEBUG#", "###########: Next power on in " +
                delayMillis / (24 * 60 * 60 * 1000) + " day," +
                delayMillis % (24 * 60 * 60 * 1000) / (60 * 60 * 1000) + " hour," +
                delayMillis % (60 * 60 * 1000) / (60 * 1000) + " min," +
                delayMillis % (60 * 1000) / 1000 + " sec")
        val commands = "echo +" + delayMillis / 1000 + " > " + RTC_DEV
        return shellExecCommand(commands, "su")
    }

    private fun _shutdown() {
        Log.d("#DEBUG#", "!!!!!!!!!! shutdown now !!!!!!!!!!")
        val command = "reboot -p"
        shellExecCommand(command, "su")
    }

    fun shutdown() {
        delayPowerOn(365 * 24 * 3600 * 1000.toLong())
        _shutdown()
    }


    fun shellExecCommand(command: String, shell: String): Boolean {
        return shellExecCommand(arrayOf(command), null, shell)
    }

    fun shellExecCommand(command: String, workingDirectory: File, shell: String): Boolean {
        return shellExecCommand(arrayOf(command), workingDirectory, shell)
    }

    fun shellExecCommand(command: Array<String>, shell: String): Boolean {
        return shellExecCommand(command, null, shell)
    }

    fun shellExecCommand(command: Array<String>?, workingDirectory: File?, shell: String?): Boolean {
        var workingDirectory = workingDirectory
        var shell = shell
        if (command == null || command.size == 0)
            return false

        var out: OutputStream? = null
        var `in`: InputStream? = null
        var err: InputStream? = null

        try {
            if (shell == null || (shell.trim { it <= ' ' }).length == 0)
                return false
            val exit = "exit\n"

            if (workingDirectory == null)
                workingDirectory = File("/")

            val runtime = Runtime.getRuntime()
            val process = runtime.exec(shell, null, workingDirectory)

            // ProcessBuilder builder = new ProcessBuilder(command);
            // builder.directory(workingDirectory);
            // builder.redirectErrorStream(true);
            // Process process = builder.start();

            val INTERVAL = 200 // 200ms
            val WAIT_TIME = 20 * 60 * 1000 // 20min

            out = process.outputStream
            for (cmd in command) {
                if (cmd != null && cmd.length > 0)
                    out!!.write(if (cmd.endsWith("\n")) cmd.toByteArray() else (cmd + "\n").toByteArray())
            }
            out!!.write(exit.toByteArray())

            val inString = StringBuffer()
            val errString = StringBuffer()

            `in` = process.inputStream
            err = process.errorStream

            var exitValue = -1

            var pass = 0
            while (pass <= WAIT_TIME) {
                try {
                    while (`in`!!.available() > 0)
                        inString.append(`in`.read().toChar())
                    while (err!!.available() > 0)
                        errString.append(err.read().toChar())

                    exitValue = -1
                    exitValue = process.exitValue()
                    break
                } catch (itex: IllegalThreadStateException) {
                    try {
                        Thread.sleep(INTERVAL.toLong())
                        pass += INTERVAL
                    } catch (e: InterruptedException) {
                        Log.e("#ERROR#", "execute command error: " + command, e)
                    }

                }

            }

            if (pass > WAIT_TIME)
                process.destroy()

            return exitValue == 0
        } catch (e: IOException) {
            Log.e("#ERROR#", "execute command failed: " + command + e.message, e)
        } finally {
            closeStream(out)
            closeStream(`in`)
            closeStream(err)
        }

        return false
    }

    private fun closeStream(closeable: Closeable?) {
        if (closeable != null) {
            try {
                closeable.close()
            } catch (e: Exception) {
            }

        }
    }
}