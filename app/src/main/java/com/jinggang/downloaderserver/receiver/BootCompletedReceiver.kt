package com.jinggang.downloaderserver.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.jinggang.downloaderserver.service.LongRunningService
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask

/**
 * Created by changfeng on 2016/3/23.
 */
class BootCompletedReceiver : BroadcastReceiver() {
    companion object {
        private const val TAG = "BootCompletedReceiver"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) {
            return
        }
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            context.startService(context.intentFor<LongRunningService>().newTask())
        }
    }
}