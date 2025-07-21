package com.example.veryoldapp

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MainModule {
    @Binds
    @Singleton
    //abstract fun bindAwsApi(impl: AwsFakeApiImpl): IAwsApi
    abstract fun bindAwsApi(impl: AwsApiImpl): IAwsApi
}
