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
    fun connect(roomName: String = "")

    fun getSocketRoomEventFlow(): Flow<SocketRoomConnectionEvents?>

    fun getSocketMessageEventFlow(): Flow<SocketMessageEvents?>

    /**
     * Emits an SDP Offer or Answer
     */
    fun sendSDPMessage(sdp: SessionDescription)

    /**
     * Emits an ice candidate
     */
    fun sendIceCandidate(iceCandidate: IceCandidate)

    /**
     * Disconnects from the socket and room
     */
    fun disconnect()
}