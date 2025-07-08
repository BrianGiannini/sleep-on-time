package io.sangui.sleepontime

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import io.sangui.sleepontime.nav.NavRoot
import io.sangui.sleepontime.ui.TimeViewModel
import org.koin.androidx.compose.koinViewModel
import androidx.wear.compose.material.MaterialTheme

class TimeActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()
            val timeViewModel: TimeViewModel = koinViewModel()

            MaterialTheme {
                NavRoot(navController = navController)
            }
        }
    }
}