package com.example.hellostatemachine.statemachine

import kotlinx.serialization.Serializable

@Serializable
data class State<T>(
    val name: String,
    val transitions: Map<String, Transition<T>>
)
