package com.webrtcdemo.webrtc_phone_app.di

import android.content.Context
import com.webrtcdemo.webrtc_phone_app.signaling.SignalingClient
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
//Goce - might be my Hilt inexperience but is WebRTCModule used
object WebRTCModule {
    @Provides
    fun providePeerConnectionClient(@ApplicationContext context: Context): PeerConnectionClient {
        return PeerConnectionClientImpl(context)
    }

    @Provides
    @WebRTCStreamReceiverQualifier
    fun provideWebRTCStreamReceiver(
        signalingClient: SignalingClient,
        peerConnectionClient: PeerConnectionClient,
        @ViewModelCoroutineScope coroutineScope: CoroutineScope
    ): BaseWebRTCStream {
        return WebRTCStreamReceiver(signalingClient, peerConnectionClient, coroutineScope)
    }

    @Provides
    @WebRTCStreamSenderQualifier
    fun provideWebRTCStreamSender(
        signalingClient: SignalingClient,
        peerConnectionClient: PeerConnectionClient,
        @ViewModelCoroutineScope coroutineScope: CoroutineScope
    ): BaseWebRTCStream {
        return WebRTCStreamSender(signalingClient, peerConnectionClient, coroutineScope)
    }
}