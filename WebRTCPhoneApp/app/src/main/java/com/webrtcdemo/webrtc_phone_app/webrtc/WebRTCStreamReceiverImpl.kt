package com.webrtcdemo.webrtc_phone_app.webrtc

import com.webrtcdemo.webrtc_phone_app.signaling.SignalingClient
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@ViewModelScoped
class WebRTCStreamReceiverImpl @Inject constructor(
    private val signalingClient: SignalingClient,
    private val peerConnectionClient: PeerConnectionClient
) : WebRTCStreamReceiver {
    private val streamEvents = MutableStateFlow<StreamEvent?>(null)

    override fun connectToRoom(roomName: String) {
        signalingClient.connect(roomName)
    }

    override fun getRoomEventFlow(): Flow<SocketRoomEvent?> = signalingClient.getSocketRoomEventFlow()

    override fun getStreamEventsFlow(): Flow<StreamEvent?> = streamEvents
}