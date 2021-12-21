package com.webrtcdemo.webrtc_phone_app.webrtc

import com.webrtcdemo.webrtc_phone_app.WebRTCAppLogger
import com.webrtcdemo.webrtc_phone_app.signaling.SignalingClient
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.webrtc.IceCandidate
import org.webrtc.SessionDescription
import org.webrtc.VideoSink
import javax.inject.Inject

@ViewModelScoped
abstract class BaseWebRTCStreamImpl(
    private val signalingClient: SignalingClient,
    private val peerConnectionClient: PeerConnectionClient,
    private val viewModelScope: CoroutineScope
) : BaseWebRTCStream {
    protected var streamEvents = MutableStateFlow(StreamEvent.NOT_STARTED)

    init {
        viewModelScope.launch {
            subscribeRoomConnectionEventFlow()
            subscribeSocketMessageEventFlow()
            subscribePeerConnectionEventFlow()
        }
    }

    override fun connectToRoom(roomName: String) {
        streamEvents.value = StreamEvent.CONNECTING
        signalingClient.connect(roomName)
    }

    override fun initVideoSink(videoSink: VideoSink) {
        peerConnectionClient.initVideoSink(videoSink)
    }

    override fun unbindVideoSink() {
        peerConnectionClient.unbindVideoSink()
    }

    private fun subscribeRoomConnectionEventFlow() {
        getRoomConnectionEventFlow()
            .filter { it != null }
            .onEach { onSocketRoomConnectionEvent(it) }
            .launchIn(viewModelScope)
    }

    private fun subscribeSocketMessageEventFlow() {
        getSocketMessageEventFlow()
            .filter { it != null }
            .onEach { onSocketMessageEvent(it) }
            .launchIn(viewModelScope)
    }

    private fun subscribePeerConnectionEventFlow() {
        getPeerConnectionEventFlow()
            .filter { it != null }
            .onEach { onPeerConnectionEvent(it) }
            .launchIn(viewModelScope)
    }

    abstract fun onSocketRoomConnectionEvent(event: SocketRoomConnectionEvents?)

    private fun onSocketMessageEvent(event: SocketMessageEvents?) {
        if (event == null) {
            return
        }

        when (event) {
            is SocketMessageEvents.AnswerReceived -> peerConnectionClient.onAnswerReceived(jsonObjectToSDP(event.data))
            is SocketMessageEvents.OfferReceived -> peerConnectionClient.onOfferReceived(jsonObjectToSDP(event.data))
            is SocketMessageEvents.IceCandidateReceived -> peerConnectionClient.onIceCandidateReceived(jsonObjectToIce(event.data))
        }
    }

    private fun jsonObjectToSDP(data: JSONObject): SessionDescription {
        return SessionDescription(
            SessionDescription.Type.fromCanonicalForm(data.getString("type").lowercase()),
            data.getString("sdp")
        )
    }

    private fun jsonObjectToIce(data: JSONObject): IceCandidate {
        return IceCandidate(
            data.getString("id"),
            data.getInt("label"),
            data.getString("candidate")
        )
    }

    private fun onPeerConnectionEvent(event: PeerConnectionEvents?) {
        if (event == null) {
            return
        }

        when (event) {
            is PeerConnectionEvents.OfferCreated -> signalingClient.sendSDPMessage(event.sdp)
            is PeerConnectionEvents.AnswerCreated -> signalingClient.sendSDPMessage(event.sdp)
            is PeerConnectionEvents.IceCandidateCreated -> signalingClient.sendIceCandidate(event.iceCandidate)
            is PeerConnectionEvents.PeerStreamConnected -> streamEvents.value = StreamEvent.CONNECTED
            is PeerConnectionEvents.PeerStreamDisconnected -> {
                cleanup()
                streamEvents.value = StreamEvent.NOT_STARTED
            }
        }
    }

    private fun cleanup() {
        signalingClient.disconnect()
        peerConnectionClient.disconnect()
    }

    override fun getRoomConnectionEventFlow(): Flow<SocketRoomConnectionEvents?> = signalingClient.getSocketRoomEventFlow()

    override fun getStreamEventFlow(): Flow<StreamEvent?> = streamEvents

    private fun getSocketMessageEventFlow() = signalingClient.getSocketMessageEventFlow()

    private fun getPeerConnectionEventFlow() = peerConnectionClient.getPeerConnectionEventFlow()
}