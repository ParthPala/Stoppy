package com.doublep.stoppy.data

import android.content.Context
import android.graphics.Color
import androidx.core.content.edit

object PrefsHelper {
    private const val PREF_NAME = "stopwatch_prefs"
    private const val KEY_TIME = "saved_time"
    private const val KEY_LAPS = "lap_list"
    private const val KEY_DIGIT_COLOR = "digit_color"
    private const val KEY_BG_COLOR = "bg_color"
    private const val KEY_FONT_NAME = "font_name"
    private const val KEY_BTN_STYLE_INDEX = "key_btn_style_index"

    fun saveState(context: Context, time: Long, laps: List<String>) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit() {
            putLong(KEY_TIME, time)
                .putStringSet(KEY_LAPS, laps.toSet())
        }
    }

    fun loadState(context: Context): Pair<Long, List<String>> {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val time = prefs.getLong(KEY_TIME, 0L)
        val laps = prefs.getStringSet(KEY_LAPS, emptySet())?.toList() ?: emptyList()
        return Pair(time, laps)
    }

    fun saveColors(context: Context, digitColor: Int, bgColor: Int) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit() {
            putInt(KEY_DIGIT_COLOR, digitColor)
                .putInt(KEY_BG_COLOR, bgColor)
        }
    }

    fun loadColors(context: Context): Pair<Int, Int> {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val digit = prefs.getInt(KEY_DIGIT_COLOR, Color.WHITE)
        val bg = prefs.getInt(KEY_BG_COLOR, Color.BLACK)
        return digit to bg
    }

    fun saveFontName(context: Context, fontID: Int) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit() { putInt(KEY_FONT_NAME, fontID) }
    }

    fun loadFontName(context: Context): Int? {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getInt(KEY_FONT_NAME, 0)
    }

    fun saveButtonStyleIndex(context: Context, index: Int) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit() { putInt(KEY_BTN_STYLE_INDEX, index) }
    }

    fun loadButtonStyleIndex(context: Context): Int {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getInt(KEY_BTN_STYLE_INDEX, 0)
    }
}