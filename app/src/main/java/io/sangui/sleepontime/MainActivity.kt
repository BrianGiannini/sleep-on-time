package io.sangui.sleepontime

import android.app.Activity
import android.content.Intent
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

            numberCycles.minValue = 1
            numberCycles.maxValue = 16
            numberCycles.value = 5

            confirmButton.setOnClickListener {
                goToTimeActivity()
            }
        }
    }

    private fun goToTimeActivity() {
        val i = Intent(applicationContext, TimeActivity::class.java)
        startActivity(i)
        finish()
    }
}