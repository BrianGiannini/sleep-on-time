package io.sangui.sleepontime.ui.parameter

import androidx.compose.runtime.State as ComposeState
import androidx.compose.runtime.mutableStateOf

class MockParameterViewModel : IParameterViewModel {
    override val selectedHour: ComposeState<Int> = mutableStateOf(7)
    override val selectedMinute: ComposeState<Int> = mutableStateOf(30)
    override val cycleLength: ComposeState<Int> = mutableStateOf(90)
    override val numberOfCycles: ComposeState<Int> = mutableStateOf(5)
    override val minutesInfo: ComposeState<String> = mutableStateOf("(7h 30min)")

    override fun updateTime(hour: Int, minute: Int) {}
    override fun updateCycleLength(newVal: Int) {}
    override fun updateNumberOfCycles(newVal: Int) {}
    override suspend fun saveStuff(
        timeWakeUpHour: Int,
        timeWakeUpMinute: Int,
        numberOfCycles: Int,
        cycleLength: Int,
    ) {}
}