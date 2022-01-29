package io.sangui.sleepontime

data class TimerData(
    val timeWakeUpHour: Int,
    val timeWakeUpMinute: Int,
    val cycleNumber: Int,
    val cycleLength: Int,
    val timeToSleep: Int
)

