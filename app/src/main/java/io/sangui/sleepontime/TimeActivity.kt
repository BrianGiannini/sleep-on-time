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

        var EtaWakeUp = if (!isNextDay) {
            chosenEtaRaw - currentTimeRaw
        } else {
            dayInMinutes - currentTimeRaw + chosenEtaRaw
        }

        val readableEtaWake = rawToReadableTime(EtaWakeUp)
        val readableEtaGoSleepRaw = rawToReadableTime(timeWantSleepRaw)

        Log.d("DEBUGMAN", "timeBeforeWakeUp: ${readableEtaWake.first}h${readableEtaWake.second}")
        Log.d("DEBUGMAN", "timeWantSleepRaw: ${readableEtaGoSleepRaw.first}h${readableEtaGoSleepRaw.second}")

        val etaSleep = EtaWakeUp - timeWantSleepRaw

        val absEtaSleep = abs(etaSleep)
        val cycleLength = timeData.cycleLength
        val etaNextCycle = absEtaSleep.mod(cycleLength)



        setupView(etaSleep, cycleLength, currentTimeRaw, chosenEtaRaw, etaNextCycle)
    }

    private fun rawToReadableTime(totalMinutes: Int): Pair<Int, Int> {
        val hours = totalMinutes.div(60)
        val minutes = abs(totalMinutes).mod(60)
        return Pair(hours, minutes)
    }

    private fun setupView(timeBeforeGoToBed: Int, cycleLength: Int, currentTime: Int, totalAlarmChosenMinutes: Int, etaNextCycle: Int) {
        val timeBeforeGoToBedReadable = rawToReadableTime(timeBeforeGoToBed)

        Log.d("DEBUGMAN", "etaNextCycle: $etaNextCycle")

        with(binding) {
            if (timeBeforeGoToBed < 0 && currentTime < totalAlarmChosenMinutes) {
                infoSleepNumber.text = "${abs(timeBeforeGoToBedReadable.first)}h ${timeBeforeGoToBedReadable.second} min"
                infoSleepText.text = getString(R.string.late_text)
            } else {
                infoSleepNumber.text = "${timeBeforeGoToBedReadable.first}h ${timeBeforeGoToBedReadable.second} min"
                infoSleepText.text = getString(R.string.before_bed_text)
            }

            if (etaNextCycle < cycleLength.div(2)) {
                displayColor(cycleLength, etaNextCycle)
                infoCycleText.text = getString(R.string.after_cycle_text)
                infoCycleNumber.text = "$etaNextCycle min"
            } else {
                val displayCycle = cycleLength - etaNextCycle
                displayColor(cycleLength, displayCycle)
                infoCycleText.text = getString(R.string.before_cycle_text)
                infoCycleNumber.text = "$displayCycle min"
            }


        }
    }

    private fun displayColor(cycleLength: Int, displayValue: Int) {
        val thresholdYes = (cycleLength * 0.1).toFloat()
        val thresholdMeh = (cycleLength * 0.2).toFloat()
        val thresholdNope = (cycleLength * 0.3).toFloat()

        with(binding) {
            when {
                displayValue > thresholdNope -> {
                    colorBackgroundTime.setBackgroundColor(getColor(R.color.nope))
                }
                displayValue > thresholdMeh -> {
                    colorBackgroundTime.setBackgroundColor(getColor(R.color.meh))
                }
                displayValue >= 0 -> {
                    colorBackgroundTime.setBackgroundColor(getColor(R.color.yes_perfect))
                }
            }
        }
    }
}