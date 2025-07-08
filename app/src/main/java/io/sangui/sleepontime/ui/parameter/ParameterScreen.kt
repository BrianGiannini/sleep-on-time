package io.sangui.sleepontime.ui.parameter

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Picker
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Vignette
import androidx.wear.compose.material.VignettePosition
import androidx.wear.compose.material.rememberPickerState
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import io.sangui.sleepontime.R
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import androidx.compose.ui.platform.LocalConfiguration
import androidx.navigation.NavHostController
import androidx.wear.compose.material3.Icon
import io.sangui.sleepontime.nav.Screen
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun ParameterScreen(
    navController: NavHostController,
    parameterViewModel: IParameterViewModel = koinViewModel<ParameterViewModel>(),
) {
    val coroutineScope = rememberCoroutineScope()

    val hours = (0..23).map { it.toString().padStart(2, '0') }
    val minutes = (0..59).map { it.toString().padStart(2, '0') }

    // Directly observe the ViewModel's State values
    val selectedHourVm by parameterViewModel.selectedHour
    val selectedMinuteVm by parameterViewModel.selectedMinute
    val cycleLengthVm by parameterViewModel.cycleLength
    val numberOfCyclesVm by parameterViewModel.numberOfCycles
    val minutesInfoVm by parameterViewModel.minutesInfo

    val cycleLengthValues = (60..110).toList()
    val numberOfCyclesValues = (1..24).toList()

    val configuration = LocalConfiguration.current
    val isScreenRound = configuration.isScreenRound

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
                bottom = 0.dp,
            )
        ) {
            item {
                Text(
                    modifier = Modifier.padding(top = 32.dp),
                    text = stringResource(id = R.string.when_get_up_text),
                    style = MaterialTheme.typography.titleLarge,
                )
            }
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Hour Picker
                    val hourPickerState = rememberPickerState(
                        initialNumberOfOptions = hours.size,
                    )

                    // LaunchedEffect to scroll picker when selectedHourVm changes
                    // This handles cases where the ViewModel updates after initial composition
                    LaunchedEffect(selectedHourVm) {
                        if (hourPickerState.selectedOption != selectedHourVm) {
                            hourPickerState.scrollToOption(selectedHourVm)
                        }
                    }

                    // LaunchedEffect to observe picker changes and update ViewModel
                    LaunchedEffect(hourPickerState) {
                        snapshotFlow { hourPickerState.selectedOption }
                            .distinctUntilChanged()
                            .collect { newOptionIndex ->
                                // Update ViewModel directly
                                parameterViewModel.updateTime(
                                    hours[newOptionIndex].toInt(),
                                    selectedMinuteVm
                                )
                            }
                    }

                    Picker(
                        state = hourPickerState,
                        contentDescription = "Select hour",
                        modifier = Modifier
                            .width(50.dp)
                            .height(75.dp),
                        readOnly = false,
                        onSelected = { /* Handled by snapshotFlow */ }
                    ) { optionIndex: Int ->
                        Text(
                            text = hours[optionIndex],
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }
                    Text(
                        text = ":",
                        style = MaterialTheme.typography.titleMedium,
                    )
                    // Minute Picker
                    val minutePickerState = rememberPickerState(
                        initialNumberOfOptions = minutes.size,
                    )

                    // LaunchedEffect to scroll picker when selectedMinuteVm changes
                    LaunchedEffect(selectedMinuteVm) {
                        if (minutePickerState.selectedOption != selectedMinuteVm) {
                            minutePickerState.scrollToOption(selectedMinuteVm)
                        }
                    }

                    // LaunchedEffect to observe picker changes and update ViewModel
                    LaunchedEffect(minutePickerState) {
                        snapshotFlow { minutePickerState.selectedOption }
                            .distinctUntilChanged()
                            .collect { newOptionIndex ->
                                // Update ViewModel directly
                                parameterViewModel.updateTime(
                                    selectedHourVm,
                                    minutes[newOptionIndex].toInt()
                                )
                            }
                    }

                    Picker(
                        state = minutePickerState,
                        contentDescription = "Select minute",
                        modifier = Modifier
                            .width(50.dp)
                            .height(100.dp),
                        readOnly = false,
                        onSelected = { /* Handled by snapshotFlow */ }
                    ) { optionIndex: Int ->
                        Text(
                            text = minutes[optionIndex],
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }
                }
            }

            item {
                Text(
                    text = stringResource(id = R.string.length_cycle_text),
                    style = MaterialTheme.typography.titleLarge,
                )
            }
            item {
                val cycleLengthPickerState = rememberPickerState(
                    initialNumberOfOptions = cycleLengthValues.size,
                )

                LaunchedEffect(cycleLengthVm) {
                    val targetIndex = cycleLengthValues.indexOf(cycleLengthVm).coerceAtLeast(0)
                    if (cycleLengthPickerState.selectedOption != targetIndex) {
                        cycleLengthPickerState.scrollToOption(targetIndex)
                    }
                }

                LaunchedEffect(cycleLengthPickerState) {
                    snapshotFlow { cycleLengthPickerState.selectedOption }
                        .distinctUntilChanged()
                        .collect { newOptionIndex ->
                            val newValue = cycleLengthValues[newOptionIndex]
                            parameterViewModel.updateCycleLength(newValue)
                        }
                }

                Picker(
                    state = cycleLengthPickerState,
                    contentDescription = "Select cycle length",
                    modifier = Modifier
                        .width(100.dp)
                        .height(75.dp),
                    readOnly = false,
                    onSelected = { /* Handled by snapshotFlow */ }
                ) { optionIndex: Int ->
                    Text(
                        text = cycleLengthValues[optionIndex].toString(),
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            }

            item {
                Text(
                    text = stringResource(id = R.string.how_many_sleep_cycles_do_you_want_to_sleep_ideally_text),
                    style = MaterialTheme.typography.titleLarge,
                )
            }
            item {
                val numberOfCyclesPickerState = rememberPickerState(
                    initialNumberOfOptions = numberOfCyclesValues.size,
                )

                LaunchedEffect(numberOfCyclesVm) {
                    val targetIndex = numberOfCyclesValues.indexOf(numberOfCyclesVm).coerceAtLeast(0)
                    if (numberOfCyclesPickerState.selectedOption != targetIndex) {
                        numberOfCyclesPickerState.scrollToOption(targetIndex)
                    }
                }

                LaunchedEffect(numberOfCyclesPickerState) {
                    snapshotFlow { numberOfCyclesPickerState.selectedOption }
                        .distinctUntilChanged()
                        .collect { newOptionIndex ->
                            val newValue = numberOfCyclesValues[newOptionIndex]
                            parameterViewModel.updateNumberOfCycles(newValue)
                        }
                }

                Picker(
                    state = numberOfCyclesPickerState,
                    contentDescription = "Select number of cycles",
                    modifier = Modifier
                        .width(100.dp)
                        .height(75.dp),
                    readOnly = false,
                    onSelected = { /* Handled by snapshotFlow */ }
                ) { optionIndex: Int ->
                    Text(
                        text = numberOfCyclesValues[optionIndex].toString(),
                        style = MaterialTheme.typography.titleLarge,
                    )
                }
            }

            item {
                Text(text = minutesInfoVm)
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Button(onClick = {
                    coroutineScope.launch {
                        // Use the current values from the ViewModel's state, which are kept up-to-date
                        parameterViewModel.saveStuff(
                            selectedHourVm,
                            selectedMinuteVm,
                            numberOfCyclesVm,
                            cycleLengthVm
                        )
                        navController.navigate(Screen.Time.route)
                    }
                }) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = stringResource(id = R.string.parameters_text_description),
                        tint = MaterialTheme.colorScheme.onPrimary,
                    )
                }
            }
        }
    }
}