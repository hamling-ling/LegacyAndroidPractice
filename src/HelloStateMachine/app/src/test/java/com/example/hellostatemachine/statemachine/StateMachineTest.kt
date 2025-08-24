package com.example.hellostatemachine.statemachine

import com.example.hellostatemachine.MyActionParam
import com.example.hellostatemachine.statemachine.StateMachine.Companion.INITIAL_STATE_NAME
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class StateMachineTest {

    private lateinit var states: StateMachineDefinition<MyActionParam>

    private val jsonString = """
        {
          "states": [
            {
              "name": "${StateMachine.INITIAL_STATE_NAME}",
              "childrenList": [],
              "transitionList": [
                {
                  "eventName": "do_something",
                  "nextStateName": "in_process",
                  "param": {
                    "param1": "param1 for initial.do_something",
                    "param2": "param2 for initial.do_something"
                  }
                }
              ]
            },
            {
              "name": "in_process",
              "childrenList": [
                {
                  "name": "preparing",
                  "childrenList": [],
                  "transitionList": [
                    {
                      "eventName": "prep_done",
                      "nextStateName": "processing",
                      "param": {
                        "param1": "param1 for in_process.preparing.prep_done",
                        "param2": "param2 for in_process.preparing.prep_done"
                      }
                    }
                  ]
                },
                {
                  "name": "processing",
                  "childrenList": [],
                  "transitionList": [
                    {
                      "eventName": "processing_done",
                      "nextStateName": "done",
                      "param": {
                        "param1": "param1 for processing.processing.processing_done",
                        "param2": "param2 for processing.processing.processing_done"
                      }
                    },
                    {
                      "eventName": "timeout",
                      "nextStateName": "fail",
                      "param": {
                        "param1": "param1 for processing.processing.timeout",
                        "param2": "param2 for processing.processing.timeout"
                      }
                    }
                  ],
                  "timeoutSec": 1
                }
              ],
              "transitionList": [
                {
                  "eventName": "interrupt",
                  "nextStateName": "fail",
                  "param": {
                    "param1": "param1 for in_process.interrupt",
                    "param2": "param2 for in_process.interrupt"
                  }
                }
              ]
            },
            {
              "name": "done",
              "childrenList": [],
              "transitionList": [
                {
                  "eventName": "reset",
                  "nextStateName": "${StateMachine.INITIAL_STATE_NAME}",
                  "param": {
                    "param1": "param1 for done.reset",
                    "param2": "param2 for done.reset"
                  }
                }
              ]
            },
            {
              "name": "fail",
              "childrenList": [],
              "transitionList": [
                {
                  "eventName": "reset",
                  "nextStateName": "${StateMachine.INITIAL_STATE_NAME}",
                  "param": {
                    "param1": "param1 for fail.reset",
                    "param2": "param2 for fail.reset"
                  }
                }
              ]
            }
          ]
        }
    """.trimIndent()

    @Before
    fun setUp() {
        states = Json.decodeFromString<StateMachineDefinition<MyActionParam>>(jsonString)

        states[INITIAL_STATE_NAME]!!.transitions["do_something"]!!.action = { param ->
            println("initial.do_something event")
            println("\tparam1:${param.param1}")
            println("\tparam2:${param.param2}")
        }

        states["in_process"]!!.children["preparing"]!!.transitions["prep_done"]!!.action = { param ->
            println("in_process.preparing.prep_done event")
            println("\tparam1:${param.param1}")
            println("\tparam2:${param.param2}")
        }

        states["in_process"]!!.children["processing"]!!.transitions["processing_done"]!!.action = { param ->
            println("in_process.processing.processing_done event")
            println("\tparam1:${param.param1}")
            println("\tparam2:${param.param2}")
        }

        states["done"]!!.transitions["reset"]!!.action = { param ->
            println("done.reset event")
            println("\tparam1:${param.param1}")
            println("\tparam2:${param.param2}")
        }

        states["fail"]!!.transitions["reset"]!!.action = { param ->
            println("fail.reset event")
            println("\tparam1:${param.param1}")
            println("\tparam2:${param.param2}")
        }
    }

    @After
    fun tearDown() {
    }

    @Test
    fun testSuccess() {
        val machine = StateMachine(states, StandardTestDispatcher())
        assertEquals(INITIAL_STATE_NAME, machine.currentStateName)

        machine.processEvent("do_something")
        assertEquals("preparing", machine.currentStateName)

        machine.processEvent("prep_done")
        assertEquals("processing", machine.currentStateName)

        machine.processEvent("processing_done")
        assertEquals("done", machine.currentStateName)

        machine.processEvent("reset")
        assertEquals(INITIAL_STATE_NAME, machine.currentStateName)
    }

    @Test
    fun testFail() {
        val machine = StateMachine(states, StandardTestDispatcher())
        assertEquals(INITIAL_STATE_NAME, machine.currentStateName)

        machine.processEvent("do_something")
        assertEquals("preparing", machine.currentStateName)

        machine.processEvent("interrupt")
        assertEquals("fail", machine.currentStateName)

        machine.processEvent("reset")
        assertEquals(INITIAL_STATE_NAME, machine.currentStateName)
    }

    @Test
    fun testTimeout() {
        val dispatcher = StandardTestDispatcher()
        runTest(dispatcher) {
            val machine = StateMachine(states, dispatcher)
            assertEquals(INITIAL_STATE_NAME, machine.currentStateName)

            machine.processEvent("do_something")
            assertEquals("preparing", machine.currentStateName)

            machine.processEvent("prep_done")
            assertEquals("processing", machine.currentStateName)

            var counter = 0
            while(machine.currentStateName == "processing" && counter < 10) {
                advanceTimeBy(1 * 1000)
                Thread.sleep(100)
                counter++
            }
            assertEquals("fail", machine.currentStateName)

            machine.processEvent("reset")
            assertEquals(INITIAL_STATE_NAME, machine.currentStateName)
        }

    }
}