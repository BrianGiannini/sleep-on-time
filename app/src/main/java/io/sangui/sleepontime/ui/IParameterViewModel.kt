package io.sangui.sleepontime.ui

interface IParameterViewModel {
    val selectedHour: Int
    val selectedMinute: Int
    val cycleLength: Int
    val numberOfCycles: Int
    val minutesInfo: String

    fun updateTime(hour: Int, minute: Int)
    fun updateCycleLength(newVal: Int)
    fun updateNumberOfCycles(newVal: Int)
    suspend fun saveStuff(
        timeWakeUpHour: Int,
        timeWakeUpMinute: Int,
        numberOfCycles: Int,
        cycleLength: Int,
    )
}