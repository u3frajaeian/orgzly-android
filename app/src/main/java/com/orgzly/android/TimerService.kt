package com.orgzly.android

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.CountDownTimer
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.orgzly.R

class TimerService : Service() {
    private val binder = TimerBinder()
    private lateinit var timer: CountDownTimer
    private val channelId = "timer_channel"

    inner class TimerBinder : Binder() {
        fun getService(): TimerService {
            return this@TimerService
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        val notif = NotificationCompat.Builder(this, channelId).setContentTitle("Timer")
            .setContentText("TimerDescription").setSmallIcon(
                R.drawable.ic_alarm
            ).build()
        startForeground(1, notif)


    }

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
    }
    fun startTimer(duration: Long, interval: Long) {
        timer = object : CountDownTimer(duration, interval) {
            override fun onTick(millisUntilFinished: Long) {
                // Update the timer in your notification
                val notification = NotificationCompat.Builder(this@TimerService, channelId)
                    .setContentTitle("Timer App")
                    .setContentText("Time left: ${millisUntilFinished / 1000} seconds")
                    .setSmallIcon(R.drawable.ic_alarm)
                    .build()

                val notificationManager =
                    getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.notify(1, notification)
            }

            override fun onFinish() {
                // Timer finished
                stopSelf()
            }
        }

        timer.start()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Timer Channel"
            val descriptionText = "Channel for Timer notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }

            val notificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}