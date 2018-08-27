package com.jinggang.downloaderserver

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import com.jinggang.downloaderserver.service.LongRunningService
import org.jetbrains.anko.defaultSharedPreferences
import org.jetbrains.anko.find
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.toast

class MainActivity : AppCompatActivity() {

    companion object {
        const val KEY_FTP_FETCH_INTERVAL = "ftp_fetch_interval"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val interval = defaultSharedPreferences.getInt(KEY_FTP_FETCH_INTERVAL, 10)

        val intervalEditText = find<EditText>(R.id.edit_text_interval)

        intervalEditText.setText(interval.toString())


        val startFtpSeverButton = find<Button>(R.id.button_start_ftp_service)
        startFtpSeverButton.setOnClickListener {
            try {
                val i = intervalEditText.text.toString().toInt()
                defaultSharedPreferences.edit().putInt(KEY_FTP_FETCH_INTERVAL, i).apply()
                startService(intentFor<LongRunningService>())
            } catch (e: Exception) {
                toast(e.message ?: "")
            }
        }
    }
}
