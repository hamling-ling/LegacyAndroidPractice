package com.example.hellostatemachine.statemachine

import kotlinx.serialization.Serializable

@Serializable
data class StateMachineDefinition<T>(
    val states: List<State<T>>
) {
    private val _stateMap: Map<String, State<T>> = states.associateBy({ it.name }, { it })

    operator fun get(key: String): State<T>? {
        return _stateMap[key]
    }
}
