package io.sangui.sleepontime.nav

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import io.sangui.sleepontime.data.DataStoreManager
import io.sangui.sleepontime.ui.parameter.ParameterScreen
import io.sangui.sleepontime.ui.time.TimeScreen
import io.sangui.sleepontime.ui.time.TimeViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.java.KoinJavaComponent.inject

sealed class Screen(val route: String) {
    object Time : Screen("time_screen")
    object Parameter : Screen("parameter_screen")
}

@Composable
fun NavRoot(navController: NavHostController) {
    val dataStoreManager: DataStoreManager by inject(DataStoreManager::class.java)
    val coroutineScope = rememberCoroutineScope()
    var startDestination by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            val firstTime = dataStoreManager.isFirstTime()
            startDestination = if (firstTime) Screen.Parameter.route else Screen.Time.route
        }
    }

    startDestination?.let { destination ->
        NavHost(navController = navController, startDestination = destination) {
            composable(Screen.Time.route) {
                TimeScreen(navController = navController, timeViewModel = koinViewModel<TimeViewModel>())
            }
            composable(Screen.Parameter.route) {
                ParameterScreen(navController = navController)
            }
        }
    }
}