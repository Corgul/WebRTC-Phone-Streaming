package com.webrtcdemo.webrtc_phone_app.signaling

import io.socket.client.IO
import io.socket.client.Socket
import javax.inject.Singleton
import com.webrtcdemo.webrtc_phone_app.WebRTCAppLogger
import com.webrtcdemo.webrtc_phone_app.webrtc.SocketRoomConnectionEvents
import com.webrtcdemo.webrtc_phone_app.webrtc.SocketMessageEvents
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import org.json.JSONObject
import org.webrtc.SessionDescription
import javax.inject.Inject
import org.json.JSONException

import org.webrtc.IceCandidate

//Goce I need you to explain this class to me in detaail
class SignalingClientImpl @Inject constructor() : SignalingClient {
    private val roomEvents = MutableStateFlow<SocketRoomConnectionEvents?>(null)
    private val messageEvents = MutableStateFlow<SocketMessageEvents?>(null)
    private val socket: Socket = IO.socket("http://webrtc-demo-signaling-server.herokuapp.com/")
    private lateinit var roomName: String

    override fun connect(roomName: String) {
        //TODO Enforce connecting only one receiver + one sender
        roomEvents.value = SocketRoomConnectionEvents.CONNECTING
        socket.connect()
        if (roomName.isNotEmpty()) {
            this.roomName = roomName
            socket.emit(CREATE_OR_JOIN, this.roomName)
        }

        setupCallbacks()
    }

    private fun setupCallbacks() {
        socket.on(Socket.EVENT_CONNECT) {
            roomEvents.value = SocketRoomConnectionEvents.CONNECTED
        }
        socket.on(Socket.EVENT_CONNECT_ERROR) {
            roomEvents.value = SocketRoomConnectionEvents.CONNECTION_ERROR
        }
        socket.on(Socket.EVENT_DISCONNECT) {
            roomEvents.value = SocketRoomConnectionEvents.DISCONNECTED
        }
        // log event
        socket.on("log") { args: Array<Any> ->
            WebRTCAppLogger.d("log call() called with: args = [" + args.contentToString() + "]")
        }
        socket.on(CREATED) {
            roomEvents.value = SocketRoomConnectionEvents.CREATED_ROOM
        }
        socket.on(JOINED) {
            roomEvents.value = SocketRoomConnectionEvents.JOINED_EXISTING_ROOM
        }
        socket.on(PEER_JOINED) {
            roomEvents.value = SocketRoomConnectionEvents.PEER_JOINED_ROOM
        }
        socket.on(FULL) {
            roomEvents.value = SocketRoomConnectionEvents.ROOM_IS_FULL
        }
        socket.on(READY) {
        }
        // SDP / ICE Candidates are exchanged through this
        socket.on(MESSAGE) { args ->
            handleMessage(args)
        }
    }

    @Synchronized
    private fun handleMessage(args: Array<Any>) {
        val data = args[0]
        if (data is String) {
            handleStringMessage(data)
        } else if (data is JSONObject) {
            handleJSONObjectMessage(data)
        }
    }

    private fun handleStringMessage(data: String) {
        WebRTCAppLogger.d("String received $data")
        if (data.equals(GOT_USER_MEDIA, true)) {
            messageEvents.value = SocketMessageEvents.GotUserMedia
        }
        if (data.equals(BYE, true)) {
            roomEvents.value = SocketRoomConnectionEvents.PEER_LEFT_ROOM
        }
    }

    private fun handleJSONObjectMessage(data: JSONObject) {
        WebRTCAppLogger.d("JSON Object received $data")
        val type = data.getString("type")

        when (type.lowercase()) {
            OFFER -> {
                Thread.sleep(1000)
                messageEvents.value = SocketMessageEvents.OfferReceived(data)
            }
            ANSWER -> {
                Thread.sleep(1000)
                messageEvents.value = SocketMessageEvents.AnswerReceived(data)
            }
            CANDIDATE -> {
                Thread.sleep(1000)
                messageEvents.value = SocketMessageEvents.IceCandidateReceived(data)
            }
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

    override fun getSocketRoomEventFlow(): Flow<SocketRoomConnectionEvents?> = roomEvents

    override fun getSocketMessageEventFlow(): Flow<SocketMessageEvents?> = messageEvents

    companion object {
        private const val CREATE_OR_JOIN = "create or join"
        private const val CREATED = "created"
        private const val JOINED = "joined"
        private const val PEER_JOINED = "peer joined"
        private const val FULL = "full"
        private const val READY = "ready"
        private const val MESSAGE = "message"
        private const val GOT_USER_MEDIA = "got user media"
        private const val BYE = "bye"
        private const val OFFER = "offer"
        private const val ANSWER = "answer"
        private const val CANDIDATE = "candidate"
        const val RECEIVER = "receiver"
        const val SENDER = "sender"
    }
}