package com.webrtcdemo.webrtc_phone_app.webrtc

enum class SocketRoomConnectionEvents {
    CONNECTING,
    CONNECTED,
    CONNECTION_ERROR,
    ROOM_IS_FULL,
    CREATED_ROOM,
    JOINED_EXISTING_ROOM,
    PEER_JOINED_ROOM,
    PEER_LEFT_ROOM,
    DISCONNECTED
}