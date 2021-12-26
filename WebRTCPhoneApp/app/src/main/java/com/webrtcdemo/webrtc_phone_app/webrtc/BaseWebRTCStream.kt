package com.webrtcdemo.webrtc_phone_app.webrtc

import kotlinx.coroutines.flow.Flow
import org.webrtc.VideoSink

interface BaseWebRTCStream {
    /**
     * Connects to the signaling server room
     * @param roomName the specific room to connect to
     */
    suspend fun connectToRoom(roomName: String)

    /**
     * Initializes the video sink for the WebRTC Library to feed frames into
     */
    fun initVideoSink(videoSink: VideoSink)

    fun unbindVideoSink()

    /**
     * Gets the flow of [SocketRoomConnectionEvents].
     * These events give information on the socket room connection status and if peers join our existing room
     */
    fun getRoomConnectionEventFlow(): Flow<SocketRoomConnectionEvents>

    /**
     * Gets the flow of [StreamEvent]
     * A [StreamEvent] is a generalized stream connection event
     */
    fun getStreamEventFlow(): Flow<StreamEvent>
}