package io.sangui.sleepontime.ui.parameter

class MockParameterViewModel : IParameterViewModel {
    override val selectedHour: Int = 7
    override val selectedMinute: Int = 30
    override val cycleLength: Int = 90
    override val numberOfCycles: Int = 5
    override val minutesInfo: String = "(7h 30min)"

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