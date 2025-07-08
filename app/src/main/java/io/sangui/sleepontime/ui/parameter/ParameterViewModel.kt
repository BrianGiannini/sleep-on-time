package io.sangui.sleepontime.ui.parameter

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.sangui.sleepontime.data.DataStoreManager
import io.sangui.sleepontime.domain.TimerData
import kotlinx.coroutines.launch

class ParameterViewModel(
    private val dataStoreManager: DataStoreManager,
) : ViewModel(), IParameterViewModel {

    override var selectedHour by mutableIntStateOf(0)
    override var selectedMinute by mutableIntStateOf(0)
    override var cycleLength by mutableIntStateOf(90)
    override var numberOfCycles by mutableIntStateOf(5)
    override var minutesInfo by mutableStateOf<String>("")

    init {
        viewModelScope.launch {
            val timeData = dataStoreManager.getTimerData()
            selectedHour = timeData?.timeWakeUpHour ?: 0
            selectedMinute = timeData?.timeWakeUpMinute ?: 0
            cycleLength = dataStoreManager.getCycleLength()
            numberOfCycles = dataStoreManager.getCycleNbr()
            timeWantSleep()
        }
    }

    override fun updateTime(hour: Int, minute: Int) {
        selectedHour = hour
        selectedMinute = minute
    }

    override fun updateCycleLength(newVal: Int) {
        cycleLength = newVal
        timeWantSleep()
    }

    override fun updateNumberOfCycles(newVal: Int) {
        numberOfCycles = newVal
        timeWantSleep()
    }

    fun timeWantSleep() {
        val numberOfMinutes = cycleLength * numberOfCycles
        var hours = 0
        var minutes = 0
        if (numberOfMinutes >= 60) {
            hours = numberOfMinutes / 60
            minutes = numberOfMinutes - (hours * 60)
        }
        minutesInfo = "(${hours}h ${minutes}min)"
    }

    override suspend fun saveStuff(timeWakeUpHour: Int, timeWakeUpMinute: Int, numberOfCycles: Int, cycleLength: Int) {
        val timerData = TimerData(
            timeWakeUpHour,
            timeWakeUpMinute,
            numberOfCycles,
            cycleLength,
            0,
        )
        dataStoreManager.saveTimerData(timerData)
    }
}