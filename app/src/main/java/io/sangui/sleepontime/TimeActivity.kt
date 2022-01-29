package io.sangui.sleepontime

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.gson.Gson
import io.sangui.sleepontime.databinding.ActivityTimeBinding
import java.util.*
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes


class TimeActivity : Activity() {

    private lateinit var binding: ActivityTimeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTimeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val preferences = applicationContext.getSharedPreferences(getString(R.string.share_prefs), Context.MODE_PRIVATE)

        val firstTime = preferences.getBoolean(getString(R.string.share_prefs_fist_time_key), true)

        Log.d("DEBUGMAN", "firstTime $firstTime")

        if (firstTime) {
            goToParameterActivity()
        }

        binding.paramsButton.setOnClickListener {
            goToParameterActivity()
        }

        val gson = Gson()
        val jsonTimerObject = preferences.getString(getString(R.string.share_prefs_timer_key), "")

        val timeData: TimerData = gson.fromJson(jsonTimerObject, TimerData::class.java)

        calculateStuff(timeData)
        setupView()
    }

    private fun goToParameterActivity() {
        val i = Intent(applicationContext, ParameterActivity::class.java)
        startActivity(i)
        finish()
    }

    private fun calculateStuff(timeData: TimerData) {

        val hourPicker = timeData.timeWakeUpHour
        val minutesPicker = timeData.timeWakeUpMinute

        Log.d("DEBUGMAN", "hours $hourPicker minutes $minutesPicker")

        val c: Calendar = Calendar.getInstance()
        val hour: Int = c.get(Calendar.HOUR_OF_DAY)
        val minute: Int = c.get(Calendar.MINUTE)

        Log.d("DEBUGMAN", "current2 hours $hour current2 minutes $minute")

    }

    private fun setupView() {
    }
}