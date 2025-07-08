package io.sangui.sleepontime.nav

sealed class Screen(val route: String) {
    object Time : Screen("time_screen")
    object Parameter : Screen("parameter_screen")
}