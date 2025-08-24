package com.example.hellostatemachine.statemachine

import kotlinx.coroutines.test.*
import kotlinx.coroutines.*
import org.junit.Assert.*
import org.junit.Test
//import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalCoroutinesApi::class)
class OneShotTimerTest {

    @Test
    fun timerCallbackExecution() {
        val dispatcher = StandardTestDispatcher()
        runTest(dispatcher) {
            var isTaskExecuted = false
            val timer = OneShotTimer(dispatcher)
            assertEquals(false, timer.isActive, "timer should not be active yet")

            timer.start(1 * 1000) {
                isTaskExecuted = true
            }
            assertEquals(true, timer.isActive, "timer should not be active yet")
            assertEquals(false, isTaskExecuted, "callback should not be called yet")

            var counter = 0
            while(timer.isActive && counter < 10) {
                advanceTimeBy(1 * 1000)
                Thread.sleep(100)
                counter++
            }
            assertEquals(false, counter == 10)

            assertEquals(true, isTaskExecuted, "callback should be called")
            assertEquals(false, timer.isActive, "timer should not be active")
        }
    }

    @Test
    fun timerCancellation() {
        val dispatcher = StandardTestDispatcher()
        runTest(dispatcher) {
            var isTaskExecuted = false
            val timer = OneShotTimer(dispatcher)
            assertEquals(false, timer.isActive, "timer should not be active yet")

            timer.start(10 * 1000) {
                isTaskExecuted = true
            }
            assertEquals(true, timer.isActive, "timer should not be active yet")
            assertEquals(false, isTaskExecuted, "callback should not be called yet")

            advanceTimeBy(1 * 1000)

            timer.cancel()
            assertEquals(false, isTaskExecuted, "callback shouldn't not be called")
            assertEquals(false, timer.isActive, "timer still should be active")
        }
    }

    @Test
    fun unstartedCancellation() {
        val dispatcher = StandardTestDispatcher()
        runTest(dispatcher) {
            val timer = OneShotTimer(dispatcher)
            assertEquals(false, timer.isActive, "timer shouldn't be active yet")

            timer.cancel()
            assertEquals(false, timer.isActive, "timer still shouldn't be called")
        }
    }
}