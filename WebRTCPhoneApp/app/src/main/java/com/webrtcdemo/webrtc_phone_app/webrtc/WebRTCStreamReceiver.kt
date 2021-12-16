package com.webrtcdemo.webrtc_phone_app.webrtc

interface WebRTCStreamReceiver {
    /**
     * Connects to the signaling server room
     * @param roomName the specific room to connect to
     */
    fun connectToRoom(roomName: String)
}