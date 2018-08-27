package com.jinggang.downloaderserver.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.tongda.commonutil.L
import org.jetbrains.anko.defaultSharedPreferences

class AppInfoReceiver : BroadcastReceiver() {

    companion object {
        const val TAG: String = "AppInfoReceiver"
    }

    private val action = "com.jinggang.action.APP_INFO"

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == action) {
            val apkMd5 = intent.getStringExtra("apk_md5")
            val edit = context.defaultSharedPreferences.edit()
            edit.putString("apk_md5", apkMd5)
                    .apply()
            L.i(TAG, "onReceive apk_md5:$apkMd5")
        }
    }
}
