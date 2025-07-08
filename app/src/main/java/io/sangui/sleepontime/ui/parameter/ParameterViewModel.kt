package io.sangui.sleepontime.ui.parameter

// Import State with the same alias
import androidx.compose.runtime.State as ComposeState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.sangui.sleepontime.data.DataStoreManager
import io.sangui.sleepontime.domain.TimerData
import kotlinx.coroutines.launch

class ParameterViewModel(
    private val dataStoreManager: DataStoreManager,
) : ViewModel(), IParameterViewModel {

    // Private MutableState for internal modification
    private val _selectedHour = mutableIntStateOf(0)
    private val _selectedMinute = mutableIntStateOf(0)
    private val _cycleLength = mutableIntStateOf(90)
    private val _numberOfCycles = mutableIntStateOf(5)

    // Public, immutable State exposed to the UI using the alias
    override val selectedHour: ComposeState<Int> = _selectedHour
    override val selectedMinute: ComposeState<Int> = _selectedMinute
    override val cycleLength: ComposeState<Int> = _cycleLength
    override val numberOfCycles: ComposeState<Int> = _numberOfCycles

    // This derived state will now automatically update and uses the alias for its type
    override val minutesInfo: ComposeState<String> = derivedStateOf {
        val numberOfMinutes = _cycleLength.intValue * _numberOfCycles.intValue
        val hours = numberOfMinutes / 60
        val minutes = numberOfMinutes % 60
        "(${hours}h ${minutes.toString().padStart(2, '0')}min)"
    }

    init {
        viewModelScope.launch {
            val loadedCycleLength = dataStoreManager.getCycleLength()
            val loadedNumberOfCycles = dataStoreManager.getCycleNbr()
            val loadedTimerData = dataStoreManager.getTimerData()

            _selectedHour.intValue = loadedTimerData?.timeWakeUpHour ?: 0
            _selectedMinute.intValue = loadedTimerData?.timeWakeUpMinute ?: 0
            _cycleLength.intValue = loadedCycleLength
            _numberOfCycles.intValue = loadedNumberOfCycles
        }
    }

    override fun updateTime(hour: Int, minute: Int) {
        _selectedHour.intValue = hour
        _selectedMinute.intValue = minute
    }

    override fun updateCycleLength(newVal: Int) {
        _cycleLength.intValue = newVal
    }

    override fun updateNumberOfCycles(newVal: Int) {
        _numberOfCycles.intValue = newVal
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