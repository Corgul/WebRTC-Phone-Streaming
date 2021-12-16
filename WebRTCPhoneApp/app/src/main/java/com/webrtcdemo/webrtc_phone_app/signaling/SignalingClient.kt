package com.webrtcdemo.webrtc_phone_app.signaling

interface SignalingClient {
    // Connects to the socket and joins the room if the name is non empty
    fun connect(roomName: String = "")
}