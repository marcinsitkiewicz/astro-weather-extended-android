package com.example.astroweatherextended

import android.content.Context

class SharedPrefUtility {
    companion object {
        private const val PREF_NAME = "preferences"

        fun getBoolData(context: Context, key: String): Boolean {
            return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getBoolean(key, false)
        }

        fun saveData(context: Context, key: String, value: Boolean) {
            context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit().putBoolean(key, value).apply()
        }

        fun getStringData(context: Context, key: String): String? {
            return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getString(key, null)
        }

        fun saveData(context: Context, key: String, value: String) {
            context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit().putString(key, value).apply()
        }

        fun getIntData(context: Context, key: String): Int {
            return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getInt(key, 0)
        }

        fun saveData(context: Context, key: String, value: Int) {
            context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit().putInt(key, value).apply()
        }

        fun getDoubleData(context: Context, key: String): Double {
            return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getFloat(key, 0F).toDouble()
        }

        fun saveData(context: Context, key: String, value: Double) {
            context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit().putFloat(key, value.toFloat()).apply()
        }
    }
}