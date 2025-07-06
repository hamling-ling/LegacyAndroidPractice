package com.example.veryoldapp

import java.util.concurrent.atomic.AtomicBoolean

abstract class IterativeRunnable<T>() : Runnable {

    abstract val ctx: T
    private val isStopRequested = AtomicBoolean(false)

    override fun run() {
        isStopRequested.set(false)

        while(isStopRequested.compareAndSet(false, false)) {
            iterativeFunc(ctx)
        }
    }

    open fun stop() {
        isStopRequested.set(true)
    }

    protected fun shouldStop() : Boolean {
        return isStopRequested.get()
    }

    abstract fun iterativeFunc(userData : T)
}
