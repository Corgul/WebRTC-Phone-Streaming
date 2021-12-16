package com.webrtcdemo.webrtc_phone_app.di

import com.webrtcdemo.webrtc_phone_app.signaling.SignalingClient
import com.webrtcdemo.webrtc_phone_app.webrtc.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@InstallIn(ViewModelComponent::class)
@Module
class WebRTCModule {
    @Provides
    fun providePeerConnectionClient(): PeerConnectionClient {
        return PeerConnectionClientImpl()
    }

    @Provides
    fun provideWebRTCStreamReceiver(
        signalingClient: SignalingClient,
        peerConnectionClient: PeerConnectionClient
    ): WebRTCStreamReceiver {
        return WebRTCStreamReceiverImpl(signalingClient, peerConnectionClient)
    }

    @Provides
    fun provideWebRTCStreamSender(
        signalingClient: SignalingClient,
        peerConnectionClient: PeerConnectionClient
    ) : WebRTCStreamSender {
        return WebRTCStreamSenderImpl(signalingClient, peerConnectionClient)
    }
}