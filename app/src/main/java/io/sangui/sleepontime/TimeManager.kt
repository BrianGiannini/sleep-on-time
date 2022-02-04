package io.sangui.sleepontime

data class TimerData(
    val timeWakeUpHour: Int,
    val timeWakeUpMinute: Int,
    val numberOfCycles: Int,
    val cycleLength: Int,
    val timeToSleep: Int
)

