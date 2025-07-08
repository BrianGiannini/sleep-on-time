package io.sangui.sleepontime.ui.time

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
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
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Icon
import androidx.compose.foundation.layout.Column
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import io.sangui.sleepontime.R
import io.sangui.sleepontime.nav.Screen


@Composable
fun TimeScreen(
    navController: NavHostController,
    timeViewModel: ITimeViewModel,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(timeViewModel.backgroundColor),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = timeViewModel.infoSleepNumber,
            style = MaterialTheme.typography.titleLarge,
        )
        Text(
            text = timeViewModel.infoSleepText,
            style = MaterialTheme.typography.bodyMedium,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = timeViewModel.infoCycleNumber,
            style = MaterialTheme.typography.titleLarge,
        )
        Text(
            text = timeViewModel.infoCycleText,
            style = MaterialTheme.typography.bodyMedium,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { navController.navigate(Screen.Parameter.route) },
            modifier = Modifier.size(48.dp),
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_baseline_settings_24),
                contentDescription = stringResource(id = R.string.parameters_text_description),
            )
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
