package io.sangui.sleepontime

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import com.google.gson.Gson
import io.sangui.sleepontime.databinding.ActivityParametersBinding


class ParameterActivity : Activity() {

    private lateinit var binding: ActivityParametersBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityParametersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val preferences = applicationContext.getSharedPreferences(getString(R.string.share_prefs), Context.MODE_PRIVATE)

        setupView(preferences)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        Log.d("DEBUGMAN", "onTouchEvent $event")

        return super.onTouchEvent(event)
    }

    private fun setupView(preferences: SharedPreferences) {
        with(binding) {
//            with(numberPickerFallAslep) {
//                minValue = 1
//                maxValue = 60
//                value = 10
//            }

            with(numberPickerSleepCycleDuration) {
                minValue = 60
                maxValue = 110
                value = 90
                setOnValueChangedListener { numberPicker, oldValue, newValue ->
                    timeWantSleep(numberCycles.value, newValue)
                }
            }

            with(numberCycles) {
                minValue = 1
                maxValue = 24
                value = 5
                setOnValueChangedListener { numberPicker, oldValue, newValue ->
                    timeWantSleep(numberPickerSleepCycleDuration.value, newValue)
                }
            }

            confirmButton.setOnClickListener {
                saveStuff(preferences)
            }

            val gson = Gson()
            val jsonTimerObject = preferences.getString(getString(R.string.share_prefs_timer_key), "")

            val timeData: TimerData? = gson.fromJson(jsonTimerObject, TimerData::class.java)

            if (timeData != null) {
                with(binding.timePickerGetUp) {
                    hour = timeData.timeWakeUpHour
                    minute = timeData.timeWakeUpMinute
                }
            }

            timeWantSleep(numberPickerSleepCycleDuration.value, numberCycles.value)
        }
    }

    private fun timeWantSleep(numberPickerSleepCycleDuration: Int, cycles: Int) {
        var hours = 0
        var minutes = 0
        val numberOfMinutes = numberPickerSleepCycleDuration * cycles
        if (numberOfMinutes >= 60) {
            hours = numberOfMinutes / 60
            minutes = numberOfMinutes - (hours * 60)
        }

        binding.numberMinutesInfo.text = "(${hours}h ${minutes}min)"
    }

    private fun saveStuff(preferences: SharedPreferences) {
        val timerData =  with(binding) {
            TimerData(
                timePickerGetUp.hour,
                timePickerGetUp.minute,
                numberCycles.value,
                numberPickerSleepCycleDuration.value,
                0
//                numberPickerFallAslep.value
            )
        }

        val gson = Gson()
        val jsonTimerData = gson.toJson(timerData)

        with(preferences) {
            edit().putString(getString(R.string.share_prefs_timer_key), jsonTimerData).apply()
            edit().putLong(getString(R.string.share_prefs_time_get_up), binding.timePickerGetUp.drawingTime).apply()
            edit().putBoolean(getString(R.string.share_prefs_fist_time_key), false).apply()
        }

        goToTimeActivity()
    }

    private fun goToTimeActivity() {
        val i = Intent(applicationContext, TimeActivity::class.java)
        startActivity(i)
        finish()
    }
}