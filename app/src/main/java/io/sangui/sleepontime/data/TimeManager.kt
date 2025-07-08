package io.sangui.sleepontime.data

import android.content.Context

class TimeManager(private val context: Context) {

    fun getString(resId: Int): String {
        return context.getString(resId)
    }

    fun getContext(): Context {
        return context
    }
}