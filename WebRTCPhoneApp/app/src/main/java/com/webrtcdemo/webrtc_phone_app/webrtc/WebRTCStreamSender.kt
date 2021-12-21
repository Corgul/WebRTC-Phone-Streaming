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
        if (event == null) {
            return
        }
        when (event) {
            SocketRoomConnectionEvents.CONNECTING -> streamEvents.value = StreamEvent.CONNECTING
            SocketRoomConnectionEvents.PEER_JOINED_ROOM -> setupPeerConnection()
            SocketRoomConnectionEvents.JOINED_EXISTING_ROOM -> setupPeerConnection()
            // TODO Add disconnect logic
        }
    }

    private fun setupPeerConnection() {
        peerConnectionClient.setupPeerConnection(EglBaseWrapper.eglBase)
        peerConnectionClient.setupCameraStreamingSupport(EglBaseWrapper.eglBase)
    }
}