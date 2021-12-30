package com.webrtcdemo.webrtc_phone_app.signaling

import io.socket.client.IO
import io.socket.client.Socket
import com.webrtcdemo.webrtc_phone_app.WebRTCAppLogger
import com.webrtcdemo.webrtc_phone_app.di.ViewModelCoroutineScope
import com.webrtcdemo.webrtc_phone_app.webrtc.SocketRoomConnectionEvents
import com.webrtcdemo.webrtc_phone_app.webrtc.SocketMessageEvents
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.webrtc.SessionDescription
import javax.inject.Inject
import org.json.JSONException

import org.webrtc.IceCandidate

class SignalingClientImpl @Inject constructor(
    @ViewModelCoroutineScope private val viewModelScope: CoroutineScope
) : SignalingClient {
    private val roomEvents = MutableSharedFlow<SocketRoomConnectionEvents>()
    private val messageEvents = MutableSharedFlow<SocketMessageEvents>()
    private val socket: Socket = IO.socket("http://webrtc-demo-signaling-server.herokuapp.com/")
    private lateinit var roomName: String

    override suspend fun connect(roomName: String) {
        roomEvents.emit(SocketRoomConnectionEvents.CONNECTING)
        //TODO Enforce connecting only one receiver + one sender
        socket.connect()
        if (roomName.isNotEmpty()) {
            this.roomName = roomName
            socket.emit(CREATE_OR_JOIN, this.roomName)
        }

        setupCallbacks()
    }

    private fun setupCallbacks() {
        socket.on(Socket.EVENT_CONNECT) { emitRoomEvent(SocketRoomConnectionEvents.CONNECTED) }
        socket.on(Socket.EVENT_CONNECT_ERROR) { emitRoomEvent(SocketRoomConnectionEvents.CONNECTION_ERROR) }
        socket.on(Socket.EVENT_DISCONNECT) { emitRoomEvent(SocketRoomConnectionEvents.DISCONNECTED) }
        socket.on("log") { args: Array<Any> ->
            WebRTCAppLogger.d("log call() called with: args = [" + args.contentToString() + "]")
        }
        socket.on(CREATED) { emitRoomEvent(SocketRoomConnectionEvents.CREATED_ROOM) }
        socket.on(JOINED) { emitRoomEvent(SocketRoomConnectionEvents.JOINED_EXISTING_ROOM) }
        socket.on(PEER_JOINED) { emitRoomEvent(SocketRoomConnectionEvents.PEER_JOINED_ROOM) }
        socket.on(FULL) { emitRoomEvent(SocketRoomConnectionEvents.ROOM_IS_FULL) }
        // SDP / ICE Candidates are exchanged through this
        socket.on(MESSAGE) { args ->
            handleMessage(args)
        }
    }

    private fun handleMessage(args: Array<Any>) {
        val data = args[0]
        if (data is JSONObject) {
            handleJSONObjectMessage(data)
        } else {
            WebRTCAppLogger.d("Unknown data type in handleMessage")
        }
    }

    private fun handleJSONObjectMessage(data: JSONObject) {
        viewModelScope.launch {
            WebRTCAppLogger.d("JSON Object received $data")
            val type = data.getString("type")

            when (type.lowercase()) {
                OFFER -> messageEvents.emit(SocketMessageEvents.OfferReceived(data))
                ANSWER -> messageEvents.emit(SocketMessageEvents.AnswerReceived(data))
                CANDIDATE -> messageEvents.emit(SocketMessageEvents.IceCandidateReceived(data))
            }
        }
    }

    private fun emitRoomEvent(event: SocketRoomConnectionEvents) {
        viewModelScope.launch {
            roomEvents.emit(event)
        }
    }

    override fun sendSDPMessage(sdp: SessionDescription) {
        try {
            WebRTCAppLogger.d("sendSDPMessage() called with sdp: $sdp")
            val json = JSONObject()
            json.put("type", sdp.type.canonicalForm())
            json.put("sdp", sdp.description)
            WebRTCAppLogger.d("sending: $json")
            socket.emit("message", json)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    override fun sendIceCandidate(iceCandidate: IceCandidate) {
        try {
            val json = JSONObject()
            json.put("type", "candidate")
            json.put("label", iceCandidate.sdpMLineIndex)
            json.put("id", iceCandidate.sdpMid)
            json.put("candidate", iceCandidate.sdp)
            WebRTCAppLogger.d("sending: $json")
            socket.emit("message", json)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun disconnect() {
        socket.emit("bye", this.roomName)
        socket.disconnect()
        socket.close()
    }

    override fun getSocketRoomEventFlow(): Flow<SocketRoomConnectionEvents> = roomEvents

    override fun getSocketMessageEventFlow(): Flow<SocketMessageEvents> = messageEvents

    companion object {
        private const val CREATE_OR_JOIN = "create or join"
        private const val CREATED = "created"
        private const val JOINED = "joined"
        private const val PEER_JOINED = "peer joined"
        private const val FULL = "full"
        private const val MESSAGE = "message"
        private const val OFFER = "offer"
        private const val ANSWER = "answer"
        private const val CANDIDATE = "candidate"
    }
}