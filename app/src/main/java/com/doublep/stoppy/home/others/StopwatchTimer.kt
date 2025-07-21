package com.doublep.stoppy.home.others

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class StopwatchTimer {
    private var job: Job? = null
    private var time = 0L

    fun start(scope: CoroutineScope, onTick: (Long) -> Unit) {
        if (job != null) return
        job = scope.launch {
            while (isActive) {
                delay(10)
                time += 10
                onTick(time)
            }
        }
    }

    fun stop() {
        job?.cancel()
        job = null
    }

    fun reset() {
        stop()
        time = 0L
    }

    fun getTime(): Long = time
    fun setTime(saved: Long) { time = saved }
}