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
              "transitionList": [
                {
                  "eventName": "do_something",
                  "nextStateName": "in_process"
                }
              ],
              "actions": [
                {
                  "param": {
                    "param1": "param1 for ${StateMachine.INITIAL_STATE_NAME} state action",
                    "param2": "param2 for ${StateMachine.INITIAL_STATE_NAME} state action"
                  }
                }
              ]
            },
            {
              "name": "in_process",
              "childrenList": [
                {
                  "name": "preparing",
                  "transitionList": [
                    {
                      "eventName": "prep_done",
                      "nextStateName": "processing"
                    }
                  ],
                  "actions": [
                    {
                      "param": {
                        "param1": "param1 for in_process.preparing state action",
                        "param2": "param2 for in_process.preparing state action"
                      }
                    }
                  ]
                },
                {
                  "name": "processing",
                  "transitionList": [
                    {
                      "eventName": "processing_done",
                      "nextStateName": "done"
                    },
                    {
                      "eventName": "timeout",
                      "nextStateName": "fail"
                    }
                  ],
                  "actions": [
                    {
                      "param": {
                        "param1": "param1 for in_process.processing state",
                        "param2": "param2 for in_process.processing state"
                      }
                    }
                  ],
                  "timeoutSec": 1
                }
              ],
              "transitionList": [
                {
                  "eventName": "interrupt",
                  "nextStateName": "fail"
                }
              ]
            },
            {
              "name": "done",
              "transitionList": [
                {
                  "eventName": "reset",
                  "nextStateName": "${StateMachine.INITIAL_STATE_NAME}"
                }
              ],
              "actions": [
                {
                  "param": {
                    "param1": "param1 for done.reset",
                    "param2": "param2 for done.reset"
                  }
                }
              ]
            },
            {
              "name": "fail",
              "transitionList": [
                {
                  "eventName": "reset",
                  "nextStateName": "${StateMachine.INITIAL_STATE_NAME}"
                }
              ],
              "actions": [
                {
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

        states[INITIAL_STATE_NAME]!!.actions[0].action = { param ->
            println("initial state action")
            println("\tparam1:${param.param1}")
            println("\tparam2:${param.param2}")
        }

        states["in_process"]!!.children["preparing"]!!.actions[0].action = { param ->
            println("in_process.preparing state action")
            println("\tparam1:${param.param1}")
            println("\tparam2:${param.param2}")
        }

        states["in_process"]!!.children["processing"]!!.actions[0].action = { param ->
            println("in_process.processing state action")
            println("\tparam1:${param.param1}")
            println("\tparam2:${param.param2}")
        }

        states["done"]!!.actions[0].action = { param ->
            println("done state action")
            println("\tparam1:${param.param1}")
            println("\tparam2:${param.param2}")
        }

        states["fail"]!!.actions[0].action = { param ->
            println("fail state event")
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