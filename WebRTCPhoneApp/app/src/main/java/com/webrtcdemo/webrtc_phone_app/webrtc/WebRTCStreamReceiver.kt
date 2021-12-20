package com.webrtcdemo.webrtc_phone_app.webrtc

import com.webrtcdemo.webrtc_phone_app.signaling.SignalingClient
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

@ViewModelScoped
class WebRTCStreamReceiver @Inject constructor(
    private val signalingClient: SignalingClient,
    private val peerConnectionClient: PeerConnectionClient,
    private val viewModelScope: CoroutineScope
) : BaseWebRTCStreamImpl(signalingClient, peerConnectionClient, viewModelScope) {

    override fun onSocketRoomConnectionEvent(event: SocketRoomConnectionEvents?) {
        if (event == null) {
            return
        }
        when (event) {
            SocketRoomConnectionEvents.PEER_JOINED_ROOM -> initiatePeerConnection()
            SocketRoomConnectionEvents.JOINED_EXISTING_ROOM -> initiatePeerConnection()
            // TODO Add disconnect logic
        }
    }

    // Move this work off of the main thread
    private fun initiatePeerConnection() {
        peerConnectionClient.setupPeerConnection(EglBaseWrapper.eglBase)
        peerConnectionClient.createOffer()
    }
}