package com.example.hellostatemachine.statemachine

import kotlinx.serialization.Serializable

@Serializable
data class Action<T> (
    val param: T,
    var action: ((param: T) -> Unit)? = null
)