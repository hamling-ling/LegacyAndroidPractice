package com.example.hellostatemachine

import kotlinx.serialization.Serializable

@Serializable
abstract class ActionParam

@Serializable
data class MyActionParam(
    val param1:String,
    val param2:String
): ActionParam()
