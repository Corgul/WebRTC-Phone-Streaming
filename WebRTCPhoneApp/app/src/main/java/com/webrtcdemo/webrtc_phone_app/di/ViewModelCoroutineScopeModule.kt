package com.webrtcdemo.webrtc_phone_app.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ViewModelCoroutineScope

@InstallIn(ViewModelComponent::class)
@Module
object ViewModelCoroutineScopeModule {

    @Provides
    @ViewModelCoroutineScope
    fun provideCoroutineScope(@MainDispatcher mainDispatcher: CoroutineDispatcher): CoroutineScope {
        return CoroutineScope(SupervisorJob() + mainDispatcher)
    }
}