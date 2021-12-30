package com.webrtcdemo.webrtc_phone_app.di

import android.content.Context
import com.webrtcdemo.webrtc_phone_app.signaling.SignalingClient
import com.webrtcdemo.webrtc_phone_app.use_case.GetIceServersUseCase
import com.webrtcdemo.webrtc_phone_app.webrtc.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import javax.inject.Qualifier

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class WebRTCStreamReceiverQualifier

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class WebRTCStreamSenderQualifier

@InstallIn(ViewModelComponent::class)
@Module
object WebRTCModule {
    @Provides
    fun providePeerConnectionClient(
        @ApplicationContext context: Context,
        @ViewModelCoroutineScope coroutineScope: CoroutineScope
    ): PeerConnectionClient {
        return PeerConnectionClientImpl(context, coroutineScope)
    }

    @Provides
    @WebRTCStreamReceiverQualifier
    fun provideWebRTCStreamReceiver(
        signalingClient: SignalingClient,
        peerConnectionClient: PeerConnectionClient,
        @ViewModelCoroutineScope coroutineScope: CoroutineScope,
        getIceServersUseCase: GetIceServersUseCase
    ): BaseWebRTCStream {
        return WebRTCStreamReceiver(signalingClient, peerConnectionClient, coroutineScope, getIceServersUseCase)
    }

    @Provides
    @WebRTCStreamSenderQualifier
    fun provideWebRTCStreamSender(
        signalingClient: SignalingClient,
        peerConnectionClient: PeerConnectionClient,
        @ViewModelCoroutineScope coroutineScope: CoroutineScope,
        getIceServersUseCase: GetIceServersUseCase
    ): BaseWebRTCStream {
        return WebRTCStreamSender(signalingClient, peerConnectionClient, coroutineScope, getIceServersUseCase)
    }
}