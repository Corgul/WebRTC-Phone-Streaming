package com.webrtcdemo.webrtc_phone_app.di

import com.webrtcdemo.webrtc_phone_app.signaling.SignalingClient
import com.webrtcdemo.webrtc_phone_app.signaling.SignalingClientImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
abstract class SignalingModule {
    @Singleton
    @Binds
    abstract fun bindsSignalingClient(signalingClientImpl: SignalingClientImpl): SignalingClient
}