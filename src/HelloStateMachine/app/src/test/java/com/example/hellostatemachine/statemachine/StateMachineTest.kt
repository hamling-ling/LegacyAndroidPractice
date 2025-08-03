package com.example.hellostatemachine.statemachine

import com.example.hellostatemachine.MyActionParam
import kotlinx.serialization.json.Json
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class StateMachineTest {

    private lateinit var states: StateMachineDefinition<MyActionParam>

    private val jsonString = """
        {
          "states": [
            {
              "name": "${StateMachine.INITIAL_STATE_NAME}",
              "transitionList": [
                {
                  "eventName": "doSomething",
                  "nextStateName": "done",
                  "param": {
                    "param1": "my param1 for initial.doSomething",
                    "param2": "my param2 for initial.doSomething"
                  }
                },
                {
                  "eventName": "doSomethingElse",
                  "nextStateName": "done",
                  "param": {
                    "param1": "my param1 for initial.doSomethingElse",
                    "param2": "my param2 for initial.doSomethingElse"
                  }
                }
              ]
            },
            {
              "name": "done",
              "transitionList": [
                {
                  "eventName": "doSomething",
                  "nextStateName": "done",
                  "param": {
                    "param1": "my param1 for done.doSomething",
                    "param2": "my param2 for done.doSomething"
                  }
                },
                {
                  "eventName": "doSomethingElse",
                  "nextStateName": "fail",
                  "param": {
                    "param1": "my param1 for done.doSomethingElse",
                    "param2": "my param2 for done.doSomethingElse"
                  }
                }
              ]
            },
            {
              "name": "fail",
              "transitionList": [
                {
                  "eventName": "doSomething",
                  "nextStateName": "done",
                  "param": {
                    "param1": "my param1 for fail.doSomething",
                    "param2": "my param2 for fail.doSomething"
                  }
                },
                {
                  "eventName": "doSomethingElse",
                  "nextStateName": "fail",
                  "param": {
                    "param1": "my param1 for fail.doSomethingElse",
                    "param2": "my param2 for fail.doSomethingElse"
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

        states["initial"]!!.transitions["doSomething"]!!.action = { param ->
            println("initial.doSomething called")
            println("\tparam1:${param.param1}")
            println("\tparam2:${param.param2}")
        }
        states["initial"]!!.transitions["doSomethingElse"]!!.action = { param ->
            println("initial.doSomethingElse called")
            println("\tparam1:${param.param1}")
            println("\tparam2:${param.param2}")
        }

        states["done"]!!.transitions["doSomething"]!!.action = { param ->
            println("done.doSomething called")
            println("\tparam1:${param.param1}")
            println("\tparam2:${param.param2}")
        }
        states["done"]!!.transitions["doSomethingElse"]!!.action = { param ->
            println("done.doSomethingElse called")
            println("\tparam1:${param.param1}")
            println("\tparam2:${param.param2}")
        }

        states["fail"]!!.transitions["doSomething"]!!.action = { param ->
            println("fail.doSomething called")
            println("\tparam1:${param.param1}")
            println("\tparam2:${param.param2}")
        }
        states["fail"]!!.transitions["doSomethingElse"]!!.action = { param ->
            println("fail.doSomethingElse called")
            println("\tparam1:${param.param1}")
            println("\tparam2:${param.param2}")
        }
    }

    @After
    fun tearDown() {
    }

    @Test
    fun processEvent() {
        val machine = StateMachine(states)
        machine.processEvent("doSomething")
        assertEquals("done", machine.currentStateName)

        machine.processEvent("doSomethingElse")
        assertEquals("fail", machine.currentStateName)

        machine.processEvent("doSomething")
        assertEquals("done", machine.currentStateName)
    }
}