package com.webrtcdemo.webrtc_phone_app.di

import com.webrtcdemo.webrtc_phone_app.signaling.SignalingClient
import com.webrtcdemo.webrtc_phone_app.signaling.SignalingClientImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.CoroutineScope

@InstallIn(ViewModelComponent::class)
@Module
object SignalingModule {
    @Provides
    fun providesSignalingClient(
        socket: Socket,
        @ViewModelCoroutineScope coroutineScope: CoroutineScope
    ): SignalingClient {
        return SignalingClientImpl(socket, coroutineScope)
    }

    @Provides
    fun provideSocket(): Socket {
        return IO.socket("http://webrtc-demo-signaling-server.herokuapp.com/")
    }
}