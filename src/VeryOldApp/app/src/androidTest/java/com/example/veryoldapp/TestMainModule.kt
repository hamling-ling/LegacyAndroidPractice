package com.example.veryoldapp

import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [MainModule::class]
)
abstract class TestMainModule {
    @Binds
    @Singleton
    abstract fun bindAwsApi(impl: AwsFakeApiImpl): IAwsApi
}
