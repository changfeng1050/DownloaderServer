package com.tongda.commonutil

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import java.io.File

/**
 * Created by chang on 2017-12-01.
 * Mail:changfeng1050@hotmail.com
 */
object AppHelper {
    val TAG: String = L.makeLogTag(AppHelper::class.java)

    fun installApkByHand(context: Context, path: String) {
        val file = File(path)
        if (file.exists()) {
            com.tongda.commonutil.L.i(ContentValues.TAG, "installApk() file:$path")
            val intent = Intent(Intent.ACTION_VIEW)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive")
            context.startActivity(intent)
        } else {
            com.tongda.commonutil.L.e(ContentValues.TAG, "installApk() file not found:$path")
        }
    }

    fun openAirPlanModeView(context: Context) {
        context.startActivity(Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS))
    }


}