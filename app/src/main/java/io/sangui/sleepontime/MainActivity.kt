package io.sangui.sleepontime

import android.app.Activity
import android.os.Bundle
import io.sangui.sleepontime.databinding.ActivityMainBinding

class MainActivity : Activity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {

            timePickerFallAslep.setIs24HourView(true)

            timePickerSleepCycleDuration.hour = 1
            timePickerSleepCycleDuration.minute = 30
            timePickerSleepCycleDuration.setIs24HourView(true)
        }
    }
}