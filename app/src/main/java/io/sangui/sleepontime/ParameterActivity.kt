package io.sangui.sleepontime

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.google.gson.Gson
import io.sangui.sleepontime.databinding.ActivityParametersBinding


class ParameterActivity : Activity() {

    private lateinit var binding: ActivityParametersBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityParametersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
    }

    private fun setupView() {
        with(binding) {
            numberPickerFallAslep.minValue = 1
            numberPickerFallAslep.maxValue = 60
            numberPickerFallAslep.value = 10

            numberPickerSleepCycleDuration.minValue = 10
            numberPickerSleepCycleDuration.maxValue = 180
            numberPickerSleepCycleDuration.value = 90
            numberPickerSleepCycleDuration.setOnValueChangedListener { numberPicker, oldValue, newValue ->
                timeWantSleep(numberCycles.value, newValue)
            }

            numberCycles.minValue = 1
            numberCycles.maxValue = 16
            numberCycles.value = 5

            numberCycles.setOnValueChangedListener { numberPicker, oldValue, newValue ->
                timeWantSleep(numberPickerSleepCycleDuration.value, newValue)
            }

            confirmButton.setOnClickListener {
                saveStuff()
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

        binding.numberMinutesInfo.text = "($hours h $minutes mins)"
    }

    private fun saveStuff() {
        val timerData = TimerData(
            binding.timePickerGetUp.hour,
            binding.timePickerGetUp.minute,
            binding.numberCycles.value,
            binding.numberPickerSleepCycleDuration.value,
            binding.numberPickerFallAslep.value
        )

        val preferences = applicationContext.getSharedPreferences(
            getString(R.string.share_prefs), Context.MODE_PRIVATE)

        val gson = Gson()
        val jsonTimerData = gson.toJson(timerData)

        preferences.edit().putString(getString(R.string.share_prefs_timer_key), jsonTimerData).apply()
        preferences.edit().putBoolean(getString(R.string.share_prefs_fist_time_key), false).apply()

        goToTimeActivity()
    }

    private fun goToTimeActivity() {
        val i = Intent(applicationContext, TimeActivity::class.java)
        startActivity(i)
        finish()
    }
}