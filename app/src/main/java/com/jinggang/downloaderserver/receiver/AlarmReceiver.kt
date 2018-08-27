package com.jinggang.downloaderserver.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.jinggang.downloaderserver.service.LongRunningService
import org.jetbrains.anko.intentFor

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        context.startService(context.intentFor<LongRunningService>())
    }
}
