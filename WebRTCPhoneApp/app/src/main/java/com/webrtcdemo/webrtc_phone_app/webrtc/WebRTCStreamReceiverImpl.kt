package com.webrtcdemo.webrtc_phone_app.webrtc

import com.webrtcdemo.webrtc_phone_app.signaling.SignalingClient
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class WebRTCStreamReceiverImpl @Inject constructor(
    signalingClient: SignalingClient,
    peerConnectionClient: PeerConnectionClient
) : WebRTCStreamReceiver {
    override fun connectToRoom(roomName: String) {

    }
}