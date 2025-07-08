package io.sangui.sleepontime.ui

import androidx.compose.ui.graphics.Color
import io.sangui.sleepontime.domain.TimerData

interface ITimeViewModel {
    var infoSleepNumber: String
    var infoCycleNumber: String
    var infoSleepText: String
    var infoCycleText: String
    var backgroundColor: Color

    fun calculateStuff(timeData: TimerData)
}