package io.sangui.sleepontime

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.wear.widget.SwipeDismissFrameLayout
import com.google.gson.Gson
import io.sangui.sleepontime.databinding.ActivityParametersBinding


class ParameterActivity : Activity()/*, SwipeDismissFrameLayout.Callback()*/ {

    private lateinit var binding: ActivityParametersBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityParametersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mCallback: SwipeDismissFrameLayout.Callback = object : SwipeDismissFrameLayout.Callback() {
            override fun onSwipeStarted(layout: SwipeDismissFrameLayout?) {
                Log.d("DEBUGMAN", "SWIPE")
                goToTimeActivity()
            }
        }

        binding.swipeLayout.addCallback(mCallback)

        val preferences = applicationContext.getSharedPreferences(getString(R.string.share_prefs), Context.MODE_PRIVATE)

        setupView(preferences)
    }

    private fun setupView(preferences: SharedPreferences) {

        with(binding) {
//            with(numberPickerFallAslep) {
//                minValue = 1
//                maxValue = 60
//                value = 10
//            }

            with(cycleDuration) {
                minValue = 60
                maxValue = 110
                value = preferences.getInt(getString(R.string.share_prefs_cycle_length_key), 90)
                setOnValueChangedListener { numberPicker, oldValue, newValue ->
                    timeWantSleep(numberCycles.value, newValue)
                }
            }

            with(numberCycles) {
                minValue = 1
                maxValue = 24
                value = preferences.getInt(getString(R.string.share_prefs_cycle_nbr_key), 5)
                setOnValueChangedListener { numberPicker, oldValue, newValue ->
                    timeWantSleep(cycleDuration.value, newValue)
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

            timeWantSleep(cycleDuration.value, numberCycles.value)
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
                cycleDuration.value,
                0
//                numberPickerFallAslep.value
            )
        }

        val gson = Gson()
        val jsonTimerData = gson.toJson(timerData)

        with(preferences) {
            edit().putString(getString(R.string.share_prefs_timer_key), jsonTimerData).apply()
            edit().putInt(getString(R.string.share_prefs_cycle_nbr_key), binding.numberCycles.value).apply()
            edit().putInt(getString(R.string.share_prefs_cycle_length_key), binding.cycleDuration.value).apply()
            edit().putLong(getString(R.string.share_prefs_time_get_up), binding.timePickerGetUp.drawingTime).apply()
            edit().putBoolean(getString(R.string.share_prefs_fist_time_key), false).apply()
        }

        goToTimeActivity()
    }

    private fun goToTimeActivity() {
        val i = Intent(applicationContext, TimeActivity::class.java)
        val leftAnimation = ActivityOptions.makeCustomAnimation(applicationContext, R.anim.anim_left_in, R.anim.anim_right_out).toBundle()
        startActivity(i, leftAnimation)
        finish()
    }
}