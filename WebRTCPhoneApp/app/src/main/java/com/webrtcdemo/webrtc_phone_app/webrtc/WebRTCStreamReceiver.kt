package com.webrtcdemo.webrtc_phone_app.webrtc

import kotlinx.coroutines.flow.Flow

interface WebRTCStreamReceiver {
    /**
     * Connects to the signaling server room
     * @param roomName the specific room to connect to
     */
    fun connectToRoom(roomName: String)

    fun getRoomEventFlow(): Flow<SocketRoomEvent?>

    fun getStreamEventsFlow(): Flow<StreamEvent?>
}