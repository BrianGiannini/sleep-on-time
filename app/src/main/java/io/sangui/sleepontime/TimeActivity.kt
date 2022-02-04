package io.sangui.sleepontime

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
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
        val chosenWakeUpTimeRaw = (hourPicker * 60) + minutesPicker

        val calendar: Calendar = Calendar.getInstance()
        val currentTimeHours = calendar.get(Calendar.HOUR_OF_DAY)
        val currentTimeMinutes = calendar.get(Calendar.MINUTE)
        val currentTimeRaw = (currentTimeHours * 60) + currentTimeMinutes

        val timeWantSleepRaw = timeData.numberOfCycles * timeData.cycleLength

        if(currentTimeRaw + timeWantSleepRaw < dayInMinutes) {
            if(currentTimeRaw + timeWantSleepRaw < chosenWakeUpTimeRaw){
                binding.infoSleepNumber.text = chosenWakeUpTimeRaw - currentTimeRaw
            } else{

            }
        } else {

        }

        val timeAvailableToSleep = (currentTimeRaw + timeWantSleepRaw).rem(dayInMinutes)

        val timeLeftBeforeGoingToSleep = chosenWakeUpTimeRaw - timeAvailableToSleep

//        val timeBeforeGoToBed = timeSurplus - timeData.timeToSleep
//        setupView(timeLeftBeforeGoingToSleep, timeData.cycleLength, currentTimeRaw, chosenWakeUpTimeRaw)
    }

    private fun rawToReadableTime(totalMinutes: Int): Pair<Int, Int> {
        val hours = totalMinutes.div(60)
        val minutes = abs(totalMinutes).mod(60)
        return Pair(hours, minutes)
    }

    private fun setupView(timeBeforeGoToBed: Int, cycleLength: Int, currentTime:Int, totalAlarmChosenMinutes: Int) {
        val timeBeforeGoToBedReadable = rawToReadableTime(timeBeforeGoToBed)

        val thresholdYes = (cycleLength * 0.1).toFloat()
        val thresholdMeh = (cycleLength * 0.2).toFloat()
        val thresholdNope = (cycleLength * 0.3).toFloat()

        with(binding) {
            if (timeBeforeGoToBed < 0 && currentTime < totalAlarmChosenMinutes) {
                infoSleepNumber.text = "${abs(timeBeforeGoToBedReadable.first)}h ${timeBeforeGoToBedReadable.second} min"
                infoSleepText.text = getString(R.string.late_text)
            } else {
                infoSleepNumber.text = "${timeBeforeGoToBedReadable.first}h ${timeBeforeGoToBedReadable.second} min"
                infoSleepText.text = getString(R.string.before_bed_text)
            }

            val absTime = abs(timeBeforeGoToBed)

            val timeBeforeNextCycle = absTime.mod(cycleLength)

                infoCycleText.text = getString(R.string.before_cycle_text)
                infoCycleNumber.text = "$timeBeforeNextCycle min"

            when {
                timeBeforeNextCycle > thresholdNope -> {
                    colorBackgroundTime.setBackgroundColor(getColor(R.color.nope))
                }
                timeBeforeNextCycle > thresholdMeh -> {
                    colorBackgroundTime.setBackgroundColor(getColor(R.color.meh))
                }
                timeBeforeNextCycle >= 0 -> {
                    colorBackgroundTime.setBackgroundColor(getColor(R.color.yes_perfect))
                }
            }
        }
    }
}