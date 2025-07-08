package io.sangui.sleepontime.ui.time

import androidx.compose.ui.graphics.Color
import io.sangui.sleepontime.domain.TimerData

class FakeTimeViewModel : ITimeViewModel {
    override var infoSleepNumber: String = "8 h 30 min"
    override var infoCycleNumber: String = "15 min"
    override var infoSleepText: String = "before bed"
    override var infoCycleText: String = "before next cycle"
    override var backgroundColor: Color = Color.White
    override fun calculateStuff(timeData: TimerData) {}
}