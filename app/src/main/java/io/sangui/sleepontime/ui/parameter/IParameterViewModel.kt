package io.sangui.sleepontime.ui.parameter

import androidx.compose.runtime.State as ComposeState

interface IParameterViewModel {
    val selectedHour: ComposeState<Int>
    val selectedMinute: ComposeState<Int>
    val cycleLength: ComposeState<Int>
    val numberOfCycles: ComposeState<Int>
    val minutesInfo: ComposeState<String>

    fun updateTime(hour: Int, minute: Int)
    fun updateCycleLength(newVal: Int)
    fun updateNumberOfCycles(newVal: Int)
    suspend fun saveStuff(timeWakeUpHour: Int, timeWakeUpMinute: Int, numberOfCycles: Int, cycleLength: Int)
}