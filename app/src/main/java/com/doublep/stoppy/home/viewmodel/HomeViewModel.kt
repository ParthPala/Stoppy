package com.doublep.stoppy.home.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.doublep.stoppy.home.others.StopwatchTimer

class HomeViewModel(app : Application) : AndroidViewModel(app) {
    val timeFormatted = MutableLiveData("00:00:00")
    val lapList = mutableListOf<String>()
    private var timer = StopwatchTimer()

    fun start() {
        timer.start(viewModelScope) { ms ->
            timeFormatted.postValue(format(ms))
        }
    }

    fun stop() = timer.stop()

    fun reset() {
        timer.reset()
        timeFormatted.value = "00:00:00"
        lapList.clear()
    }

    fun lap() {
        val lapTime = format(timer.getTime())
        lapList.add(0, lapTime)
    }

    fun format(ms: Long): String {
        val sec = ms / 1000
        val min = sec / 60
        val remSec = sec % 60
        val remMs = (ms % 1000) / 10
        return "%02d:%02d:%02d".format(min, remSec, remMs)
    }

    fun getTime() = timer.getTime()
    fun setTimerTime(time: Long) = timer.setTime(time)
}