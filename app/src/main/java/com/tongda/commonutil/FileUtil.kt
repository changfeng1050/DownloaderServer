package com.tongda.commonutil

import android.util.Log
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.channels.FileChannel
import java.nio.charset.Charset

/**
 * Created by Zhou Jinlong on 2018/5/23.
 */
class FileUtil {
    companion object {

        private val TAG: String = "FileUtil"

        fun deleteFile(path: String) {
            val file = File(path)
            file.deleteRecursively()
        }

        fun deleteFile(file: File) {
            file.deleteRecursively()
        }

        fun readFile(filePath: String, charset: Charset = Charsets.UTF_8): String {
            val file = File(filePath)
            return readFile(file, charset)
        }

        fun readFile(file: File, charset: Charset = Charsets.UTF_8): String {
            return try {
                val readText = file.readText(charset)
                readText
            } catch (e: Exception) {
                ""
            }
        }

        @JvmStatic
        fun getFileSize(file: File): Long {
            var s: Long = 0
            try {
                if (file.exists()) {
                    val fis = FileInputStream(file)
                    s = fis.available().toLong()
                    fis.close()
                } else {
                    return 0
                }
            } catch (e: Exception) {

            }

            return s
        }

        @JvmStatic
        fun getFileSize(path: String): Long {
            return getFileSize(File(path))
        }


        // 不会抛出异常
        @Throws(IOException::class)
        fun copyFileUsingFileChannels(source: File, dest: File) {
            var inputChannel: FileChannel? = null
            var outputChannel: FileChannel? = null
            try {
                inputChannel = FileInputStream(source).channel
                outputChannel = FileOutputStream(dest).channel
                outputChannel!!.transferFrom(inputChannel, 0, inputChannel!!.size())
            } catch (e: Exception) {
                Log.e(TAG, "copyFileUsingFileChannels() from:" + source.absolutePath + " to: " + dest.absolutePath, e)
            } finally {
                try {
                    inputChannel!!.close()
                } catch (e: Exception) {
                }

                try {
                    outputChannel!!.close()
                } catch (e: Exception) {

                }

            }
        }

        // 会抛出异常
        @Throws(IOException::class)
        fun copyFileUsingFileChannels2(source: File, dest: File) {
            var inputChannel: FileChannel? = null
            var outputChannel: FileChannel? = null
            try {
                inputChannel = FileInputStream(source).channel
                outputChannel = FileOutputStream(dest).channel
                outputChannel!!.transferFrom(inputChannel, 0, inputChannel!!.size())
            } catch (e: Exception) {
                throw e
            } finally {
                try {
                    inputChannel!!.close()
                } catch (e: Exception) {
                }

                try {
                    outputChannel!!.close()
                } catch (e: Exception) {

                }

            }
        }
    }
}