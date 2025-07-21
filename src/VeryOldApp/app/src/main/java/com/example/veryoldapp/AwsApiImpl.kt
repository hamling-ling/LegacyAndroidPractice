package com.example.veryoldapp

import javax.inject.Inject

class AwsApiImpl @Inject constructor() : IAwsApi {
    override fun callApi(): String {
        return "real api call!"
    }
}