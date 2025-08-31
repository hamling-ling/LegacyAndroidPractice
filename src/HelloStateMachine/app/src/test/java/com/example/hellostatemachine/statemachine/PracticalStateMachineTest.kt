package com.example.hellostatemachine.statemachine

import com.example.hellostatemachine.GoToActionParam
import com.example.hellostatemachine.PlayContentActionParam
import com.example.hellostatemachine.PracticalActionParam
import com.example.hellostatemachine.RequestTimeBatteryActionParam
import com.example.hellostatemachine.createSerializerModule
import kotlinx.serialization.json.Json
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PracticalStateMachineTest {

    @Before
    fun setUp() {
    }

    @After
    fun tearDown() {
    }

    @Test
    fun serializeRequestTimeBatteryActionParam() {
        val format = Json { serializersModule = createSerializerModule() }
        val param = format.decodeFromString<PracticalActionParam>(
            """
            {
                "type": "RequestTimeBatteryActionParam",
                "actionType": "RequestTimeBattery"
            }
            """.trimIndent()
        )
        assertTrue(param is RequestTimeBatteryActionParam)
        assertEquals(param.actionType, "RequestTimeBattery")
    }

    @Test
    fun serializePlayContentActionParam() {
        val format = Json { serializersModule = createSerializerModule() }
        val param = format.decodeFromString<PracticalActionParam>(
            """
            {
                "type": "PlayContentActionParam",
                "actionType": "PlayContents",
                "contents": ["a.mp4", "b.mp4"]
            }
            """.trimIndent()
        )
        assertTrue(param is PlayContentActionParam)
        assertEquals(param.actionType, "PlayContents")
        val actionParam = param as PlayContentActionParam
        assertEquals(actionParam.contents.size, 2)
        assertEquals(actionParam.contents[0], "a.mp4")
        assertEquals(actionParam.contents[1], "b.mp4")
    }

    @Test
    fun serializeGoToActionParam() {
        val format = Json { serializersModule = createSerializerModule() }
        val param = format.decodeFromString<PracticalActionParam>(
            """
            {
                "type": "GoToActionParam",
                "actionType": "GoTo",
                "location": "Kobe"
            }
            """.trimIndent()
        )
        assertTrue(param is GoToActionParam)
        val actionParam = param as GoToActionParam
        assertEquals(actionParam.actionType, "GoTo")
        assertEquals(actionParam.location, "Kobe")
    }

    @Test
    fun serializeState() {
        val format = Json { serializersModule = createSerializerModule() }
        val state = format.decodeFromString<State<PracticalActionParam>>(
            """
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
                    "type": "GoToActionParam",
                    "actionType": "GoTo",
                    "location": "Hawaii"
                  }
                }
              ]
            }
            """.trimIndent()
        )
        val param = state.actions.first().param
        assertTrue(param is GoToActionParam)

        val actionParam = param as GoToActionParam
        assertEquals(actionParam.actionType, "GoTo")
        assertEquals(actionParam.location, "Hawaii")
    }

    @Test
    fun serializeStates() {
        val format = Json { serializersModule = createSerializerModule() }
        val stateMachine = format.decodeFromString<StateMachineDefinition<PracticalActionParam>>(
            """
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
                            "type": "GoToActionParam",
                            "actionType": "GoTo",
                            "location": "Hawaii"
                          }
                        }
                      ]
                  },
                  {
                      "name": "in_process",
                      "transitionList": [
                        {
                          "eventName": "reset",
                          "nextStateName": "${StateMachine.INITIAL_STATE_NAME}"
                        }
                      ],
                      "actions": [
                        {
                          "param": {
                            "type": "GoToActionParam",
                            "actionType": "GoTo",
                            "location": "Hawaii"
                          }
                        }
                      ]
                    }
                ]
            }
            """.trimIndent()
        )
        val param = stateMachine.states.first().actions.first().param
        assertTrue(param is GoToActionParam)

        val actionParam = param as GoToActionParam
        assertEquals(actionParam.actionType, "GoTo")
        assertEquals(actionParam.location, "Hawaii")
    }
}