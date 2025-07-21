package com.example.veryoldapp

import javax.inject.Inject

class AwsFakeApiImpl @Inject constructor() : IAwsApi {
    override fun callApi(): String {
        return "fake api call!"
    }
}