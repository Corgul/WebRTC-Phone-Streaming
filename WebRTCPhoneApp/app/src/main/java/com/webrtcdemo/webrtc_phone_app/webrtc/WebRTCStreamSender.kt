package com.webrtcdemo.webrtc_phone_app.webrtc

import com.webrtcdemo.webrtc_phone_app.signaling.SignalingClient
import com.webrtcdemo.webrtc_phone_app.use_case.GetIceServersUseCase
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

@ViewModelScoped
class WebRTCStreamSender @Inject constructor(
    signalingClient: SignalingClient,
    private val peerConnectionClient: PeerConnectionClient,
    viewModelScope: CoroutineScope,
    getIceServersUseCase: GetIceServersUseCase
) : BaseWebRTCStreamImpl(signalingClient, peerConnectionClient, viewModelScope, getIceServersUseCase) {

    override fun auxiliaryPeerConnectionSetup() {
        peerConnectionClient.setupCameraStreamingSupport(EglBaseWrapper.eglBase)
    }
}