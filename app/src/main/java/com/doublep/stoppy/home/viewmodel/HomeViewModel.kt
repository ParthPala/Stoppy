package com.doublep.stoppy.home.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.doublep.stoppy.home.others.StopwatchTimer

class HomeViewModel(app : Application) : AndroidViewModel(app) {

    // LiveData for displaying formatted time in the UI
    val timeFormatted = MutableLiveData("00:00:00")
    // List to hold lap times
    val lapList = mutableListOf<String>()

    private var timer = StopwatchTimer()

    // Starts the stopwatch timer
    fun start() {
        timer.start(viewModelScope) { ms ->
            timeFormatted.postValue(format(ms))
        }
    }

    fun stop() = timer.stop()

    // Resets the stopwatch to 00:00:00 and clears lap data
    fun reset() {
        timer.reset()
        timeFormatted.value = "00:00:00"
        lapList.clear()
    }

    // Adds the current time to lap list
    fun lap() {
        val lapTime = format(timer.getTime())
        lapList.add(0, lapTime)
    }

    // Updates the time display
    fun format(ms: Long): String {
        val sec = ms / 1000
        val min = sec / 60
        val remSec = sec % 60
        val remMs = (ms % 1000) / 10
        return "%02d:%02d:%02d".format(min, remSec, remMs)
    }

    fun getTime() = timer.getTime()

    // Restores saved seconds after lifecycle pause/resume
    fun setTimerTime(time: Long) = timer.setTime(time)
}