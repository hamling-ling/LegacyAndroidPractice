package com.example.hellostatemachine.statemachine

import kotlinx.serialization.Serializable

@Serializable
data class Transition<T>(
    val event: String,
    val nextStateName: String,
    val param: T?,
    var action: ((param: T) -> Unit)? = null
)
