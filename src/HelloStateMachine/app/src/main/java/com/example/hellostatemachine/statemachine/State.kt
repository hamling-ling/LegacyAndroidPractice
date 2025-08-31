package com.example.hellostatemachine.statemachine

import kotlinx.serialization.Serializable

@Serializable
data class State<T>(
    val name: String,
    val childrenList: List<State<T>> = listOf(),
    val transitionList: List<Transition<T>> = listOf(),
    val timeoutSec: Long? = null
) {
    val children: Map<String, State<T>> = childrenList.associateBy({it.name}, {it})
    val transitions: Map<String, Transition<T>> = transitionList.associateBy({it.eventName}, {it})
    val actions: List<Action<T>> = listOf()

    operator fun get(key: String): Transition<T>? {
        return transitions[key]
    }
}
