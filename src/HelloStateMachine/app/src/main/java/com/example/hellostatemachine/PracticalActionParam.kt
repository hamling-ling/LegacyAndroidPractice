package com.example.hellostatemachine

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass


@Serializable
abstract class PracticalActionParam {
    abstract val actionType: String
}

@Serializable
@SerialName("BasicActionParam")
data class BasicActionParam(
    override val actionType: String
) : PracticalActionParam()


@Serializable
@SerialName("RequestTimeBatteryActionParam")
data class RequestTimeBatteryActionParam(
    override val actionType: String
) : PracticalActionParam()


@Serializable
@SerialName("PlayContentActionParam")
data class PlayContentActionParam(
    override val actionType: String,
    val contents: List<String>
) : PracticalActionParam()


@Serializable
@SerialName("GoToActionParam")
data class GoToActionParam(
    override val actionType: String,
    val location: String
) : PracticalActionParam()


fun createSerializerModule() : SerializersModule {
    return SerializersModule {
        polymorphic(PracticalActionParam::class) {
            subclass(RequestTimeBatteryActionParam::class)
            subclass(PlayContentActionParam::class)
            subclass(GoToActionParam::class)
            defaultDeserializer { BasicActionParam.serializer() }
        }
    }
}

