package com.webrtcdemo.webrtc_phone_app.signaling

import com.webrtcdemo.webrtc_phone_app.webrtc.SocketRoomEvent
import kotlinx.coroutines.flow.Flow

interface SignalingClient {
    /**
     * Connects to the socket and joins the room if the name is non empty
     */
    fun connect(roomName: String = "")

    fun getSocketRoomEventFlow(): Flow<SocketRoomEvent?>
}