package io.sangui.sleepontime

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import com.google.gson.Gson
import io.sangui.sleepontime.databinding.ActivityTimeBinding
import java.util.*
import kotlin.math.abs


class TimeActivity : Activity() {

    private lateinit var binding: ActivityTimeBinding
    private lateinit var tickReceiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTimeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.paramsButton.setOnClickListener {
            goToParameterActivity()
        }
    }

    override fun onResume() {
        super.onResume()

        val preferences = applicationContext.getSharedPreferences(getString(R.string.share_prefs), Context.MODE_PRIVATE)

        val firstTime = preferences.getBoolean(getString(R.string.share_prefs_fist_time_key), true)

        if (firstTime) {
            goToParameterActivity()
        }

        val gson = Gson()
        val jsonTimerObject = preferences.getString(getString(R.string.share_prefs_timer_key), "")

        val timeData: TimerData? = gson.fromJson(jsonTimerObject, TimerData::class.java)

        if (timeData != null) {
            calculateStuff(timeData)
        }

        tickReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action?.compareTo(Intent.ACTION_TIME_TICK) == 0) {
                    timeData?.let { calculateStuff(it) }
                }
            }
        }
        registerReceiver(tickReceiver, IntentFilter(Intent.ACTION_TIME_TICK))

    }

    private fun goToParameterActivity() {
        val i = Intent(applicationContext, ParameterActivity::class.java)
        startActivity(i)
        finish()
    }

    override fun onPause() {
        super.onPause()

        unregisterReceiver(tickReceiver)
    }

    private fun calculateStuff(timeData: TimerData) {
        val dayInMinutes = 1440

        val hourPicker = timeData.timeWakeUpHour
        val minutesPicker = timeData.timeWakeUpMinute
        // Chosen wake up time
        val chosenEtaRaw = (hourPicker * 60) + minutesPicker

        val calendar: Calendar = Calendar.getInstance()
        val currentTimeHours = calendar.get(Calendar.HOUR_OF_DAY)
        val currentTimeMinutes = calendar.get(Calendar.MINUTE)
        // Current Time
        val currentTimeRaw = (currentTimeHours * 60) + currentTimeMinutes

        // Time length want sleep
        val timeWantSleepRaw = timeData.numberOfCycles * timeData.cycleLength

        // Know if next day
        val isNextDay = chosenEtaRaw < currentTimeRaw

        val etaWakeUp = if (!isNextDay) {
            chosenEtaRaw - currentTimeRaw
        } else {
            dayInMinutes - currentTimeRaw + chosenEtaRaw
        }

        Log.d("DEBUGMAN", "alarm is next day: $isNextDay")

        val readableEtaWake = rawToReadableTime(etaWakeUp)
        val readableEtaGoSleepRaw = rawToReadableTime(timeWantSleepRaw)

        Log.d("DEBUGMAN", "timeBeforeWakeUp: ${readableEtaWake.first}h${readableEtaWake.second}")
        Log.d("DEBUGMAN", "timeWantSleepRaw: ${readableEtaGoSleepRaw.first}h${readableEtaGoSleepRaw.second}")

        val etaSleep = etaWakeUp - timeWantSleepRaw

        var isAfterCycle: Boolean
        var etaNextCycle: Int

        val isAfterSleep: Boolean = etaSleep < 0

        val absEtaSleep = abs(etaSleep)
        val cycleLength = timeData.cycleLength

        Log.d("DEBUGMAN", "absEtaSleep $absEtaSleep")

//        if (absEtaSleep < cycleLength ) {
//            etaNextCycle = absEtaSleep
//            if (etaNextCycle > cycleLength.div(2)) {
//                isAfterCycle = true
//            } else {
//                isAfterCycle = false
//            }
//        } else { // sup cycle
            etaNextCycle = absEtaSleep.mod(cycleLength)
            if (etaNextCycle < cycleLength.div(2)) {
                isAfterCycle = false
            }else {
                isAfterCycle = true
                etaNextCycle = etaNextCycle - cycleLength.div(2)
            }
//        }

        Log.d("DEBUGMAN", "etaNextCycle: $etaNextCycle")

        setupView(etaSleep, cycleLength, currentTimeRaw, chosenEtaRaw, etaNextCycle, isAfterCycle, isAfterSleep)
    }

    private fun rawToReadableTime(totalMinutes: Int): Pair<Int, Int> {
        val hours = totalMinutes.div(60)
        val minutes = abs(totalMinutes).mod(60)
        return Pair(hours, minutes)
    }

    private fun setupView(etaSleep: Int, cycleLength: Int, currentTime: Int, totalAlarmChosenMinutes: Int,
                          etaNextCycle: Int, isAfterCycle: Boolean, isAfterSleep: Boolean) {
        val timeBeforeGoToBedReadable = rawToReadableTime(etaSleep)

        with(binding) {
            if (isAfterSleep) {
                infoSleepNumber.text = "${abs(timeBeforeGoToBedReadable.first)} h ${timeBeforeGoToBedReadable.second} min"
                infoSleepText.text = getString(R.string.late_text)
            } else {
                infoSleepNumber.text = "${timeBeforeGoToBedReadable.first} h ${timeBeforeGoToBedReadable.second} min"
                infoSleepText.text = getString(R.string.before_bed_text)
            }

            if (isAfterCycle) {
                infoCycleText.text = getString(R.string.after_cycle_text)
                infoCycleNumber.text = "$etaNextCycle min"
            } else {
                infoCycleText.text = getString(R.string.before_cycle_text)
                infoCycleNumber.text = "$etaNextCycle min"
            }

            displayColor(cycleLength, etaNextCycle, isAfterCycle)

        }

    }

    private fun displayColor(cycleLength: Int, etaNextCycle: Int, isAfterCycle: Boolean) {
        val threshold10 = (cycleLength * 0.1).toFloat()
        val threshold20 = (cycleLength * 0.2).toFloat()
        val threshold30 = (cycleLength * 0.3).toFloat()

        with(binding) {
            if (isAfterCycle) {
                when {
                    etaNextCycle > threshold10 -> {
                        colorBackgroundTime.setBackgroundColor(getColor(R.color.nope))
                    }
                    else -> {
                        colorBackgroundTime.setBackgroundColor(getColor(R.color.meh))
                    }
                }
            } else {
                when {
                    etaNextCycle > threshold30 -> {
                        colorBackgroundTime.setBackgroundColor(getColor(R.color.nope))
                    }
                    etaNextCycle > threshold20 -> {
                        colorBackgroundTime.setBackgroundColor(getColor(R.color.meh))
                    }
                    etaNextCycle >= threshold10 -> {
                        colorBackgroundTime.setBackgroundColor(getColor(R.color.yes_perfect))
                    }
                    etaNextCycle >= 0 -> {
                        colorBackgroundTime.setBackgroundColor(getColor(R.color.meh))
                    }
                }
            }

        }
    }
}