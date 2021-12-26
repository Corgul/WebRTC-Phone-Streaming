package com.webrtcdemo.webrtc_phone_app.signaling

import com.webrtcdemo.webrtc_phone_app.webrtc.SocketMessageEvents
import com.webrtcdemo.webrtc_phone_app.webrtc.SocketRoomConnectionEvents
import kotlinx.coroutines.flow.Flow
import org.webrtc.IceCandidate
import org.webrtc.SessionDescription

interface SignalingClient {
    /**
     * Connects to the socket and joins the room if the name is non empty
     */
    suspend fun connect(roomName: String = "")

    /**
     * Gets the flow of [SocketRoomConnectionEvents].
     * These events give information on the socket room connection status and if peers join our existing room
     */
    fun getSocketRoomEventFlow(): Flow<SocketRoomConnectionEvents>

    /**
     * Gets the flow of [SocketMessageEvents]
     * These messages contain peer information to plug into the WebRTC Library such as IceCandidates and SDP Offer/Answer
     */
    fun getSocketMessageEventFlow(): Flow<SocketMessageEvents>

    /**
     * Emits an SDP Offer or Answer to the socket
     */
    fun sendSDPMessage(sdp: SessionDescription)

    /**
     * Emits an ice candidate to the socket
     */
    fun sendIceCandidate(iceCandidate: IceCandidate)

    /**
     * Disconnects from the socket and room
     */
    fun disconnect()
}