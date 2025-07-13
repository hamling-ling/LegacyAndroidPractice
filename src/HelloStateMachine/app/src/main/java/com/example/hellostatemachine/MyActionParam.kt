package com.example.hellostatemachine

import com.example.hellostatemachine.statemachine.ActionParam
import kotlinx.serialization.Serializable

@Serializable
data class MyActionParam(
    val param1:String,
    val param2:String
): ActionParam()
