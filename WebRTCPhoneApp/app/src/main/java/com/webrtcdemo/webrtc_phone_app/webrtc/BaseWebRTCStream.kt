package com.webrtcdemo.webrtc_phone_app.webrtc

import kotlinx.coroutines.flow.Flow
import org.webrtc.VideoSink

interface BaseWebRTCStream {
    /**
     * Connects to the signaling server room
     * @param roomName the specific room to connect to
     */
    suspend fun connectToRoom(roomName: String)

    fun initVideoSink(videoSink: VideoSink)

    fun unbindVideoSink()

    fun getRoomConnectionEventFlow(): Flow<SocketRoomConnectionEvents>

    fun getStreamEventFlow(): Flow<StreamEvent>
}