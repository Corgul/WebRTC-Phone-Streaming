package com.webrtcdemo.webrtc_phone_app.di

import com.webrtcdemo.webrtc_phone_app.signaling.SignalingClient
import com.webrtcdemo.webrtc_phone_app.signaling.SignalingClientImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import kotlinx.coroutines.CoroutineScope

@InstallIn(ViewModelComponent::class)
@Module
object SignalingModule {
    @Provides
    fun providesSignalingClient(@ViewModelCoroutineScope coroutineScope: CoroutineScope): SignalingClient {
        return SignalingClientImpl(coroutineScope)
    }
}