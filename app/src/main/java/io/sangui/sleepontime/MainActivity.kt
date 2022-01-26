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

        setupView()
    }

    private fun setupView() {
        with(binding) {
            numberPickerFallAslep.minValue = 1
            numberPickerFallAslep.maxValue = 60
            numberPickerFallAslep.value = 10

            numberPickerSleepCycleDuration.minValue = 10
            numberPickerSleepCycleDuration.maxValue = 180
            numberPickerSleepCycleDuration.value = 90
        }
    }
}