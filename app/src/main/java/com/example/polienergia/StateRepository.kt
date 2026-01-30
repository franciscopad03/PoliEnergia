package com.example.polienergia

import android.content.Context
import org.json.JSONObject

object StateRepository {

    private const val PREFS_NAME = "PoliEnergiaPrefs"
    private const val KEY_BATTERY_LEVEL = "battery_level"
    private const val KEY_GENERATION_POWER = "generation_power"
    private const val KEY_CONSUMPTION_POWER = "consumption_power"
    private const val KEY_BATTERY_CAPACITY_LEVEL = "battery_capacity_level"

    // --- Battery, Power, and Capacity --- //

    fun getBatteryLevel(context: Context): Float {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getFloat(KEY_BATTERY_LEVEL, 0f)
    }

    fun saveBatteryLevel(context: Context, level: Float) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
        prefs.putFloat(KEY_BATTERY_LEVEL, level)
        prefs.apply()
    }

    fun saveGenerationPower(context: Context, power: Float) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
        prefs.putFloat(KEY_GENERATION_POWER, power)
        prefs.apply()
    }

    fun getGenerationPower(context: Context): Float {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getFloat(KEY_GENERATION_POWER, 0f)
    }

    fun saveTotalConsumptionPower(context: Context, power: Int) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
        prefs.putInt(KEY_CONSUMPTION_POWER, power)
        prefs.apply()
    }

    fun getTotalConsumptionPower(context: Context): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getInt(KEY_CONSUMPTION_POWER, 0)
    }

    fun saveBatteryCapacityLevel(context: Context, level: Int) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
        prefs.putInt(KEY_BATTERY_CAPACITY_LEVEL, level)
        prefs.apply()
    }

    fun getBatteryCapacityLevel(context: Context): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getInt(KEY_BATTERY_CAPACITY_LEVEL, 1) // Default to medium (1)
    }

    // --- UI State Persistence --- //

    fun saveUiState(context: Context, screenKey: String, stateMap: Map<String, Int>) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
        val json = JSONObject(stateMap as Map<*, *>).toString()
        prefs.putString("ui_state_$screenKey", json)
        prefs.apply()
    }

    fun loadUiState(context: Context, screenKey: String): Map<String, Int> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString("ui_state_$screenKey", null)
        if (json != null) {
            try {
                val jsonObject = JSONObject(json)
                val stateMap = mutableMapOf<String, Int>()
                val keys = jsonObject.keys()
                while (keys.hasNext()) {
                    val key = keys.next()
                    stateMap[key] = jsonObject.getInt(key)
                }
                return stateMap
            } catch (e: Exception) {
                return emptyMap() // En caso de error de parseo, devuelve vacío
            }
        }
        return emptyMap()
    }
}