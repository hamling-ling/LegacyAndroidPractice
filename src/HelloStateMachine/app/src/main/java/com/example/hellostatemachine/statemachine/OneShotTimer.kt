package com.example.hellostatemachine.statemachine

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.locks.ReentrantLock

/**
 * A simple cancelable timer that executes a task after a specified delay.
 * @param dispatcher The CoroutineScope to launch the timer in.
 */
class OneShotTimer(private val dispatcher: CoroutineDispatcher) {
    private val _scope: CoroutineScope = CoroutineScope(SupervisorJob() + dispatcher)
    private var _job: Job? = null
    private val _lock = ReentrantLock()

    val isActive: Boolean
        get() = _job?.isActive == true

    fun start(delayMillis: Long, callback: (timer: OneShotTimer)->Unit) {
        synchronized(_lock) {
            if (isActive) {
                return@start
            }
            _job = _scope.launch {
                delay(delayMillis)
                synchronized(_lock) {
                    if (isActive) {
                        callback(this@OneShotTimer)
                        // null　を代入し isActive フラグを下ろす
                        _job = null
                    }
                }
            }
        }
    }

    fun cancel() {
        synchronized(_lock) {
            _job?.cancel()
            _job = null
        }
    }
}
