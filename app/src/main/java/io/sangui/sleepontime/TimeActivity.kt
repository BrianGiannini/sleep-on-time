package io.sangui.sleepontime

import android.app.Activity
import android.os.Bundle
import io.sangui.sleepontime.databinding.ActivityTimeBinding

class TimeActivity : Activity() {

    private lateinit var binding: ActivityTimeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTimeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
    }

    private fun setupView() {
    }
}