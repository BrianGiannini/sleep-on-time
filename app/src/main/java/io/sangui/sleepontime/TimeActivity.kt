package io.sangui.sleepontime

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.gson.Gson
import io.sangui.sleepontime.databinding.ActivityTimeBinding
import java.util.*


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
    }

    private fun goToParameterActivity() {
        val i = Intent(applicationContext, ParameterActivity::class.java)
        startActivity(i)
        finish()
    }

    private fun calculateStuff(timeData: TimerData) {
        val dayInMinutes = 1440

        val hourPicker = timeData.timeWakeUpHour
        val minutesPicker = timeData.timeWakeUpMinute
        val totalChosenMinutes = hourPicker + (minutesPicker * 60)

        Log.d("DEBUGMAN", "hours $hourPicker minutes $minutesPicker")

        val calendar: Calendar = Calendar.getInstance()
        val currentTimeHours = calendar.get(Calendar.HOUR_OF_DAY)
        val currentTimeMinutes = calendar.get(Calendar.MINUTE)
        val totalCurrentMinutes = currentTimeHours + (currentTimeMinutes * 60)

        Log.d("DEBUGMAN", "current2 hours $currentTimeHours current2 minutes $currentTimeMinutes")

        val currentTimesToMinutes = currentTimeHours * currentTimeMinutes
        val timeMinutesWantSleep = timeData.cycleNumber * timeData.cycleLength

        val timeAvailableToSleep = (totalCurrentMinutes + totalChosenMinutes).rem(dayInMinutes)

        setupView()

    }

    private fun setupView() {
        binding.colorBackgroundTime.resources.getColor(R.color.nope, applicationContext.theme)
    }
}