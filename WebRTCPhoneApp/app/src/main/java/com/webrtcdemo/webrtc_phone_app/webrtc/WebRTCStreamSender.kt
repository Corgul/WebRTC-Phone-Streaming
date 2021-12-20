package com.webrtcdemo.webrtc_phone_app.webrtc

import com.webrtcdemo.webrtc_phone_app.signaling.SignalingClient
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

@ViewModelScoped
class WebRTCStreamSender @Inject constructor(
    private val signalingClient: SignalingClient,
    private val peerConnectionClient: PeerConnectionClient,
    private val viewModelScope: CoroutineScope
) : BaseWebRTCStreamImpl(signalingClient, peerConnectionClient, viewModelScope) {
    override fun onSocketRoomConnectionEvent(event: SocketRoomConnectionEvents?) {
        // TODO Implement later
    }
}