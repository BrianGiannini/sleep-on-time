package io.sangui.sleepontime.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import io.sangui.sleepontime.domain.TimerData
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = Constants.SHARE_PREFS)

class DataStoreManager(private val context: Context, private val gson: Gson) {

    private object PreferencesKeys {
        val TIMER_DATA = stringPreferencesKey(Constants.SHARE_PREFS_TIMER_KEY)
        val CYCLE_NBR = intPreferencesKey(Constants.SHARE_PREFS_CYCLE_NBR_KEY)
        val CYCLE_LENGTH = intPreferencesKey(Constants.SHARE_PREFS_CYCLE_LENGTH_KEY)
        val FIRST_TIME = booleanPreferencesKey(Constants.SHARE_PREFS_FIRST_TIME_KEY)
    }

    suspend fun saveTimerData(timerData: TimerData) {
        try {
            context.dataStore.edit { preferences ->
                preferences[PreferencesKeys.TIMER_DATA] = gson.toJson(timerData)
                preferences[PreferencesKeys.CYCLE_NBR] = timerData.numberOfCycles
                preferences[PreferencesKeys.CYCLE_LENGTH] = timerData.cycleLength
                preferences[PreferencesKeys.FIRST_TIME] = false
            }
        } catch (e: Exception) { }
    }

    suspend fun getTimerData(): TimerData? {
        return context.dataStore.data.map { preferences ->
            val json = preferences[PreferencesKeys.TIMER_DATA]
            val data = if (json != null) {
                gson.fromJson(json, TimerData::class.java)
            } else {
                null
            }
            data
        }.first()
    }

    suspend fun getCycleNbr(): Int {
        return context.dataStore.data.map { preferences ->
            val value = preferences[PreferencesKeys.CYCLE_NBR] ?: 5
            value
        }.first()
    }

    suspend fun getCycleLength(): Int {
        return context.dataStore.data.map { preferences ->
            val value = preferences[PreferencesKeys.CYCLE_LENGTH] ?: 90
            value
        }.first()
    }

    suspend fun isFirstTime(): Boolean {
        return context.dataStore.data.map { preferences ->
            val value = preferences[PreferencesKeys.FIRST_TIME] ?: true
            value
        }.first()
    }
}