package io.sangui.sleepontime

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import io.sangui.sleepontime.nav.NavRoot
import androidx.wear.compose.material.MaterialTheme

class TimeActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()

            MaterialTheme {
                NavRoot(navController = navController)
            }
        }
    }
}