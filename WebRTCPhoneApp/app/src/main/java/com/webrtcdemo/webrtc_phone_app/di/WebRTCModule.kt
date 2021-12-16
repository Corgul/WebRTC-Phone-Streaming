package com.webrtcdemo.webrtc_phone_app.di

import com.webrtcdemo.webrtc_phone_app.webrtc.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class WebRTCStreamSenderListener

@InstallIn(ViewModelComponent::class)
@Module
abstract class WebRTCModule {
    @Binds
    abstract fun providePeerConnectionClient(peerConnectionClientImpl: PeerConnectionClientImpl): PeerConnectionClient

    @Binds
    abstract fun provideWebRTCStreamReceiver(webRTCStreamReceiverImpl: WebRTCStreamReceiverImpl): WebRTCStreamReceiver

    @Binds
    abstract fun provideWebRTCStreamSender(webRTCStreamSenderImpl: WebRTCStreamSenderImpl): WebRTCStreamSender
}