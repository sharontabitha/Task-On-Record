package com.taskonrecord.data

import android.content.Context

object PreferencesHelper {
    private const val PREFS_NAME = "taskonrecord_prefs"
    private const val KEY_THEME = "app_theme"
    private const val KEY_XP = "village_xp"
    private const val KEY_GOLD = "village_gold"

    fun getTheme(context: Context): String {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getString(KEY_THEME, "DARK_SLATE") ?: "DARK_SLATE"
    }

    fun setTheme(context: Context, theme: String) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().putString(KEY_THEME, theme).apply()
    }

    fun getXp(context: Context): Int {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getInt(KEY_XP, 0)
    }

    fun addXp(context: Context, amount: Int): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val current = prefs.getInt(KEY_XP, 0) + amount
        prefs.edit().putInt(KEY_XP, current).apply()
        return current
    }

    fun getGold(context: Context): Int {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getInt(KEY_GOLD, 100)
    }

    fun addGold(context: Context, amount: Int) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val current = prefs.getInt(KEY_GOLD, 100) + amount
        prefs.edit().putInt(KEY_GOLD, current).apply()
    }
}
