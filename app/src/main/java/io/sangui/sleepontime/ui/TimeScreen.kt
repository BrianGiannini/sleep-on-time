package io.sangui.sleepontime.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.wear.compose.material.*
import io.sangui.sleepontime.R
import io.sangui.sleepontime.domain.TimerData
import io.sangui.sleepontime.nav.Screen

class FakeTimeViewModel : ITimeViewModel {
    override var infoSleepNumber: String = "8 h 30 min"
    override var infoCycleNumber: String = "15 min"
    override var infoSleepText: String = "before bed"
    override var infoCycleText: String = "before next cycle"
    override var backgroundColor: Color = Color.White

    override fun calculateStuff(timeData: TimerData) {
        // Do nothing for preview
    }
}

@Composable
fun TimeScreen(
    navController: NavHostController,
    timeViewModel: ITimeViewModel
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(timeViewModel.backgroundColor)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = timeViewModel.infoSleepNumber,
                style = MaterialTheme.typography.title1
            )
            Text(
                text = timeViewModel.infoSleepText,
                style = MaterialTheme.typography.body2
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = timeViewModel.infoCycleNumber,
                style = MaterialTheme.typography.title1
            )
            Text(
                text = timeViewModel.infoCycleText,
                style = MaterialTheme.typography.body2
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { navController.navigate(Screen.Parameter.route) },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_baseline_settings_24),
                    contentDescription = stringResource(id = R.string.parameters_text_description),
                )
            }
        }
    }
}

@Preview(showBackground = true, device = "id:wearos_small_round")
@Composable
fun PreviewTimeScreen() {
    TimeScreen(
        navController = rememberNavController(),
        timeViewModel = FakeTimeViewModel().apply {
            infoSleepNumber = "8 h 30 min"
            infoSleepText = "before bed"
            infoCycleNumber = "15 min"
            infoCycleText = "before next cycle"
            backgroundColor = Color.Black
        }
    )
}
