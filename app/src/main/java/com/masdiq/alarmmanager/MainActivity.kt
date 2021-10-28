package com.masdiq.alarmmanager

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var picker: MaterialTimePicker
    private lateinit var calendar: Calendar
    private lateinit var alarm: AlarmManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        notificationChannel()

        btn_select.setOnClickListener() {
            showTimePicker()
        }

        btn_set.setOnClickListener() {
            setAlarm()
        }

        btn_cancel.setOnClickListener() {
            cancelAlarm()
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun cancelAlarm() {
        alarm = getSystemService(ALARM_SERVICE) as AlarmManager
        val move = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, move, 0)
        alarm.cancel(pendingIntent)

        Toast.makeText(this, "Alarm Cancelled", Toast.LENGTH_LONG).show()
    }

    @SuppressLint("UnspecifiedImmutableFlag", "ShowToast")
    private fun setAlarm() {
        alarm = getSystemService(ALARM_SERVICE) as AlarmManager
        val move = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, move, 0)
        alarm.setRepeating(
            AlarmManager.RTC_WAKEUP, calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY, pendingIntent
        )
        Toast.makeText(this, "Alarm set Successfully", Toast.LENGTH_SHORT).show()
    }

    @SuppressLint("SetTextI18n")
    private fun showTimePicker() {
        picker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(12)
            .setMinute(0)
            .setTitleText("Android")
            .build()
        picker.show(supportFragmentManager, "Android")
        picker.addOnPositiveButtonClickListener {
            if (picker.hour > 12) {
                time.text =
                    String.format("%02d", picker.hour - 12) + " : " + String.format(
                        "%02d", picker.minute
                    ) + "PM"
            } else {
                time.text = String.format("%02d", picker.hour) + " : " + String.format(
                    "%02d", picker.minute
                ) + "AM"
            }

            calendar = Calendar.getInstance()
            calendar[Calendar.HOUR_OF_DAY] = picker.hour
            calendar[Calendar.MINUTE] = picker.minute
            calendar[Calendar.SECOND] = 0
            calendar[Calendar.MILLISECOND] = 0
        }
    }

    private fun notificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = "androidReminderChannel"
            val description = "Channel for Alarm Manager"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("android", name, importance)
            channel.description = description
            val notificationManager = getSystemService(
                NotificationManager::class.java
            )

            notificationManager.createNotificationChannel(channel)
        }
    }
}