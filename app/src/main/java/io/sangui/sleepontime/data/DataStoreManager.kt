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

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class DataStoreManager(private val context: Context, private val gson: Gson) {

    private object PreferencesKeys {
        val TIMER_DATA = stringPreferencesKey("timer_data")
        val CYCLE_NBR = intPreferencesKey("cycle_nbr")
        val CYCLE_LENGTH = intPreferencesKey("cycle_length")
        val FIRST_TIME = booleanPreferencesKey("first_time")
    }

    suspend fun saveTimerData(timerData: TimerData) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.TIMER_DATA] = gson.toJson(timerData)
            preferences[PreferencesKeys.CYCLE_NBR] = timerData.numberOfCycles
            preferences[PreferencesKeys.CYCLE_LENGTH] = timerData.cycleLength
            preferences[PreferencesKeys.FIRST_TIME] = false
        }
    }

    suspend fun getTimerData(): TimerData? {
        return context.dataStore.data.map { preferences ->
            val json = preferences[PreferencesKeys.TIMER_DATA]
            if (json != null) {
                gson.fromJson(json, TimerData::class.java)
            } else {
                null
            }
        }.first()
    }

    suspend fun getCycleNbr(): Int {
        return context.dataStore.data.map { preferences ->
            preferences[PreferencesKeys.CYCLE_NBR] ?: 5
        }.first()
    }

    suspend fun getCycleLength(): Int {
        return context.dataStore.data.map { preferences ->
            preferences[PreferencesKeys.CYCLE_LENGTH] ?: 90
        }.first()
    }

    suspend fun isFirstTime(): Boolean {
        return context.dataStore.data.map { preferences ->
            preferences[PreferencesKeys.FIRST_TIME] ?: true
        }.first()
    }
}