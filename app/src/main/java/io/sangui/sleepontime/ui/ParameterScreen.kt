package io.sangui.sleepontime.ui

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Picker
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.Vignette
import androidx.wear.compose.material.VignettePosition
import androidx.wear.compose.material.rememberPickerState
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import io.sangui.sleepontime.R
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import androidx.compose.ui.platform.LocalConfiguration
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@Composable
fun ParameterScreen(
    navController: NavHostController,
    parameterViewModel: IParameterViewModel = koinViewModel<ParameterViewModel>()
) {
    val coroutineScope = rememberCoroutineScope()

    val hours = (0..23).map { it.toString().padStart(2, '0') }
    val minutes = (0..59).map { it.toString().padStart(2, '0') }

    val selectedHourIndex = remember { mutableIntStateOf(parameterViewModel.selectedHour) }
    val selectedMinuteIndex = remember { mutableIntStateOf(parameterViewModel.selectedMinute) }

    val cycleLengthValues = (60..110).toList()
    val numberOfCyclesValues = (1..24).toList()

    val selectedCycleLengthIndex = remember { mutableIntStateOf(cycleLengthValues.indexOf(parameterViewModel.cycleLength)) }
    val selectedNumberOfCyclesIndex = remember { mutableIntStateOf(numberOfCyclesValues.indexOf(parameterViewModel.numberOfCycles)) }

    // Determine screen shape for adaptive padding
    val configuration = LocalConfiguration.current
    val isScreenRound = configuration.isScreenRound

    // Define vertical padding based on screen shape
    val verticalContentPadding = if (isScreenRound) 24.dp else 8.dp
    val horizontalContentPadding = 16.dp

    val scalingLazyListState = rememberScalingLazyListState()

    Scaffold(
        vignette = {
            Vignette(vignettePosition = VignettePosition.TopAndBottom)
        },
        positionIndicator = {
            PositionIndicator(
                scalingLazyListState = scalingLazyListState
            )
        }
    ) {
        ScalingLazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = scalingLazyListState,
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(
                start = horizontalContentPadding,
                end = horizontalContentPadding,
                top = verticalContentPadding,
                bottom = verticalContentPadding
            )
        ) {
            item {
                Text(text = stringResource(id = R.string.when_get_up_text))
            }
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val hourPickerState = rememberPickerState(initialNumberOfOptions = hours.size, initiallySelectedOption = selectedHourIndex.intValue)
                    Picker(
                        state = hourPickerState,
                        contentDescription = "Select hour",
                        modifier = Modifier
                            .width(50.dp)
                            .height(100.dp),
                        readOnly = false,
                        onSelected = {
                            selectedHourIndex.intValue = hourPickerState.selectedOption
                            parameterViewModel.updateTime(hours[hourPickerState.selectedOption].toInt(), minutes[selectedMinuteIndex.intValue].toInt())
                        }
                    ) { optionIndex: Int ->
                        Text(text = hours[optionIndex])
                    }
                    Text(text = ":")
                    val minutePickerState = rememberPickerState(initialNumberOfOptions = minutes.size, initiallySelectedOption = selectedMinuteIndex.intValue)
                    Picker(
                        state = minutePickerState,
                        contentDescription = "Select minute",
                        modifier = Modifier
                            .width(50.dp)
                            .height(100.dp),
                        readOnly = false,
                        onSelected = {
                            selectedMinuteIndex.intValue = minutePickerState.selectedOption
                            parameterViewModel.updateTime(hours[selectedHourIndex.intValue].toInt(), minutes[minutePickerState.selectedOption].toInt())
                        }
                    ) { optionIndex: Int ->
                        Text(text = minutes[optionIndex])
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Text(text = stringResource(id = R.string.length_cycle_text))
            }
            item {
                val cycleLengthPickerState = rememberPickerState(initialNumberOfOptions = cycleLengthValues.size, initiallySelectedOption = selectedCycleLengthIndex.intValue)
                Picker(
                    state = cycleLengthPickerState,
                    contentDescription = "Select cycle length",
                    modifier = Modifier
                        .width(100.dp)
                        .height(100.dp), // Keep fixed height for Picker
                    readOnly = false,
                    onSelected = {
                        selectedCycleLengthIndex.intValue = cycleLengthPickerState.selectedOption
                        parameterViewModel.updateCycleLength(cycleLengthValues[cycleLengthPickerState.selectedOption])
                    }
                ) { optionIndex: Int ->
                    Text(text = cycleLengthValues[optionIndex].toString())
                }
            }

            item {
                Text(text = stringResource(id = R.string.how_many_sleep_cycles_do_you_want_to_sleep_ideally_text))
            }
            item {
                val numberOfCyclesPickerState = rememberPickerState(
                    initialNumberOfOptions = numberOfCyclesValues.size,
                    initiallySelectedOption = selectedNumberOfCyclesIndex.intValue
                )
                Picker(
                    state = numberOfCyclesPickerState,
                    contentDescription = "Select number of cycles",
                    modifier = Modifier
                        .width(100.dp)
                        .height(100.dp), // Keep fixed height for Picker
                    readOnly = false,
                    onSelected = {
                        selectedNumberOfCyclesIndex.intValue = numberOfCyclesPickerState.selectedOption
                        parameterViewModel.updateNumberOfCycles(numberOfCyclesValues[numberOfCyclesPickerState.selectedOption])
                    }
                ) { optionIndex: Int ->
                    Text(text = numberOfCyclesValues[optionIndex].toString())
                }
            }

            item {
                Text(text = parameterViewModel.minutesInfo)
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Button(onClick = {
                    coroutineScope.launch {
                        parameterViewModel.saveStuff(
                            hours[selectedHourIndex.intValue].toInt(),
                            minutes[selectedMinuteIndex.intValue].toInt(),
                            numberOfCyclesValues[selectedNumberOfCyclesIndex.intValue],
                            cycleLengthValues[selectedCycleLengthIndex.intValue]
                        )
                        navController.popBackStack()
                    }
                }) {
                    Text(text = stringResource(id = R.string.confirm_text))
                }
            }
        }
    }
}

@Preview(showBackground = true, device = "id:wearos_small_round")
@Composable
fun PreviewParameterScreen() {
    ParameterScreen(
        navController = rememberNavController(),
        parameterViewModel = MockParameterViewModel(),
    )

}