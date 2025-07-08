package io.sangui.sleepontime.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.sangui.sleepontime.data.DataStoreManager
import io.sangui.sleepontime.R
import io.sangui.sleepontime.data.TimeManager
import io.sangui.sleepontime.domain.TimerData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*
import kotlin.math.abs

class TimeViewModel(
    private val timeManager: TimeManager,
    private val dataStoreManager: DataStoreManager,
) : ViewModel(), ITimeViewModel {

    override var infoSleepNumber by mutableStateOf("")
    override var infoCycleNumber by mutableStateOf("")
    override var infoCycleText by mutableStateOf("")
    override var infoSleepText by mutableStateOf("")
    override var backgroundColor by mutableStateOf(Color.White)

    private val _timerData = MutableStateFlow<TimerData?>(null)
    val timerData: StateFlow<TimerData?> = _timerData

    private var tickReceiver: BroadcastReceiver

    init {
        viewModelScope.launch {
            dataStoreManager.getTimerData()?.let {
                _timerData.value = it
                calculateStuff(it)
            }
        }

        tickReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action?.compareTo(Intent.ACTION_TIME_TICK) == 0) {
                    _timerData.value?.let { calculateStuff(it) }
                }
            }
        }
        timeManager.getContext().registerReceiver(tickReceiver, IntentFilter(Intent.ACTION_TIME_TICK))
    }

    override fun onCleared() {
        super.onCleared()
        timeManager.getContext().unregisterReceiver(tickReceiver)
    }


    override fun calculateStuff(timeData: TimerData) {
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

        val etaSleep = etaWakeUp - timeWantSleepRaw

        val isAfterCycle: Boolean
        var etaNextCycle: Int

        val isAfterSleep: Boolean = etaSleep < 0

        val absEtaSleep = abs(etaSleep)
        val cycleLength = timeData.cycleLength

        etaNextCycle = absEtaSleep.mod(cycleLength)

        if (etaSleep < 0 && absEtaSleep < cycleLength.div(2)) {
            isAfterCycle = true
        } else if (etaSleep < 0 && absEtaSleep > cycleLength.div(2) && absEtaSleep < cycleLength) {
            isAfterCycle = false
            etaNextCycle = cycleLength - etaNextCycle
        } else if (etaSleep > 0 && absEtaSleep < cycleLength.div(2)) {
            isAfterCycle = false
        } else if (etaNextCycle < cycleLength.div(2)) {
            isAfterCycle = false
        } else {
            isAfterCycle = true
            etaNextCycle = cycleLength - etaNextCycle
        }

        val timeBeforeGoToBedReadable = rawToReadableTime(etaSleep)

        if (isAfterSleep) {
            infoSleepNumber = "${abs(timeBeforeGoToBedReadable.first)} h ${timeBeforeGoToBedReadable.second} min"
            infoSleepText = timeManager.getString(R.string.late_text)
        } else {
            infoSleepNumber = "${timeBeforeGoToBedReadable.first} h ${timeBeforeGoToBedReadable.second} min"
            infoSleepText = timeManager.getString(R.string.before_bed_text)
        }

        if (isAfterCycle) {
            infoCycleText = timeManager.getString(R.string.after_cycle_text)
            infoCycleNumber = "$etaNextCycle min"
        } else {
            infoCycleText = timeManager.getString(R.string.before_cycle_text)
            infoCycleNumber = "$etaNextCycle min"
        }

        val threshold10 = (cycleLength * 0.1).toFloat()
        val threshold20 = (cycleLength * 0.2).toFloat()
        val threshold30 = (cycleLength * 0.3).toFloat()

        backgroundColor = if (isAfterCycle) {
            when {
                etaNextCycle > threshold10 -> {
                    if (abs(etaSleep) < cycleLength) {
                        Color(0xFF2b0005) // nope
                    } else {
                        Color(0xFF2b0005) // nope_light
                    }
                }
                else -> {
                    if (abs(etaSleep) < cycleLength) {
                        Color(0xFF242300) // meh
                    } else {
                        Color(0xFF242300) // meh_light
                    }
                }
            }
        } else {
            if (abs(etaSleep) < cycleLength) {
                when {
                    etaNextCycle > threshold30 -> {
                        Color(0xFF2b0005) // nope
                    }
                    etaNextCycle > threshold20 -> {
                        Color(0xFF242300) // meh
                    }
                    etaNextCycle >= threshold10 -> {
                        Color(0xFF002b02) // yes
                    }
                    else -> {
                        Color(0xFF242300) // meh
                    }
                }
            } else {
                when {
                    etaNextCycle > threshold30 -> {
                        Color(0xFF2b0005) // nope_light
                    }
                    etaNextCycle > threshold20 -> {
                        Color(0xFF242300) // meh_light
                    }
                    etaNextCycle >= threshold10 -> {
                        Color(0xFF002b02) // yes_light
                    }
                    else -> {
                        Color(0xFF242300) // meh_light
                    }
                }
            }
        }
    }

    private fun rawToReadableTime(totalMinutes: Int): Pair<Int, Int> {
        val hours = totalMinutes.div(60)
        val minutes = abs(totalMinutes).mod(60)
        return Pair(hours, minutes)
    }
}
