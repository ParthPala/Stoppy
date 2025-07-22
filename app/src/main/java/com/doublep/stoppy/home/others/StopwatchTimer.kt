package com.doublep.stoppy.home.others

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

// StopwatchTimer is a helper class that runs a stopwatch-like timer using coroutines
class StopwatchTimer {
    // holds the coroutines job reference so we can cancel it later
    private var job: Job? = null

    // Tracks elapsed time in milliseconds
    private var time = 0L

    fun start(scope: CoroutineScope, onTick: (Long) -> Unit) {
        // Prevent multiple coroutines from being launched
        if (job != null) return

        // Launch a coroutine that updates the time every 10 milliseconds
        job = scope.launch {
            while (isActive) {
                delay(10)
                time += 10
                onTick(time) // Notify UI
            }
        }
    }

    // Stops the stopwatch by cancelling the coroutine job
    fun stop() {
        job?.cancel()
        job = null
    }

    // Resets the stopwatch to 0 and cancels the current job
    fun reset() {
        stop()
        time = 0L
    }

    fun getTime(): Long = time
    fun setTime(saved: Long) { time = saved }
}