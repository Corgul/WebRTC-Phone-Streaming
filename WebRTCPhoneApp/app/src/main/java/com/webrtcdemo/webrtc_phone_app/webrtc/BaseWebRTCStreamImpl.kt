package com.webrtcdemo.webrtc_phone_app.webrtc

import com.webrtcdemo.webrtc_phone_app.WebRTCAppLogger
import com.webrtcdemo.webrtc_phone_app.common.Resource
import com.webrtcdemo.webrtc_phone_app.signaling.SignalingClient
import com.webrtcdemo.webrtc_phone_app.use_case.GetIceServersUseCase
import com.webrtcdemo.webrtc_phone_app.webrtc.extensions.toIceCandidate
import com.webrtcdemo.webrtc_phone_app.webrtc.extensions.toSessionDescription
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.webrtc.IceCandidate
import org.webrtc.SessionDescription
import org.webrtc.VideoSink

@ViewModelScoped
abstract class BaseWebRTCStreamImpl(
    private val signalingClient: SignalingClient,
    private val peerConnectionClient: PeerConnectionClient,
    private val viewModelScope: CoroutineScope,
    private val getIceServersUseCase: GetIceServersUseCase
) : BaseWebRTCStream {
    private var streamEvents = MutableSharedFlow<StreamEvent>()

    init {
        viewModelScope.launch {
            streamEvents.emit(StreamEvent.NOT_STARTED)
            subscribeRoomConnectionEventFlow()
            subscribeSocketMessageEventFlow()
            subscribePeerConnectionEventFlow()
        }
    }

    override suspend fun connectToRoom(roomName: String) {
        streamEvents.emit(StreamEvent.CONNECTING)
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
            .onEach { onSocketRoomConnectionEvent(it) }
            .launchIn(viewModelScope)
    }

    private fun subscribeSocketMessageEventFlow() {
        getSocketMessageEventFlow()
            .onEach { onSocketMessageEvent(it) }
            .launchIn(viewModelScope)
    }

    private fun subscribePeerConnectionEventFlow() {
        getPeerConnectionEventFlow()
            .onEach { onPeerConnectionEvent(it) }
            .launchIn(viewModelScope)
    }

    private fun onSocketRoomConnectionEvent(event: SocketRoomConnectionEvents) {
        when (event) {
            SocketRoomConnectionEvents.PEER_JOINED_ROOM -> setupPeerConnection()
            SocketRoomConnectionEvents.JOINED_EXISTING_ROOM -> setupPeerConnection()
            // TODO Add disconnect logic
        }
    }

    /**
     * Left to inherit since the Sender and Receiver will both have their own specific setup behavior
     */
    private fun setupPeerConnection() {
        viewModelScope.launch {
            // Only setup the peer connection after we have attempted to get the list of ice servers from the API
            getIceServersUseCase().collect { result ->
                when (result) {
                    is Resource.Success -> peerConnectionClient.setupPeerConnection(EglBaseWrapper.eglBase, result.data)
                    is Resource.Error -> {
                        WebRTCAppLogger.d("Error received from API: ${result.message}")
                        peerConnectionClient.setupPeerConnection(EglBaseWrapper.eglBase)
                    }
                }
                auxiliaryPeerConnectionSetup()
            }
        }
    }

    /**
     * Inherit if there are any other setup methods that need to be called after setupPeerConnection has completed
     * Should always be called after [PeerConnectionClient.setupPeerConnection]
     */
    protected abstract fun auxiliaryPeerConnectionSetup()

    private suspend fun onSocketMessageEvent(event: SocketMessageEvents) {
        when (event) {
            is SocketMessageEvents.AnswerReceived -> peerConnectionClient.onAnswerReceived(jsonObjectToSDP(event.data))
            is SocketMessageEvents.OfferReceived -> peerConnectionClient.onOfferReceived(jsonObjectToSDP(event.data))
            is SocketMessageEvents.IceCandidateReceived -> peerConnectionClient.onIceCandidateReceived(jsonObjectToIce(event.data))
        }
    }

    private fun jsonObjectToSDP(data: JSONObject) = data.toSessionDescription()

    private fun jsonObjectToIce(data: JSONObject) = data.toIceCandidate()

    private suspend fun onPeerConnectionEvent(event: PeerConnectionEvents) {
        when (event) {
            is PeerConnectionEvents.OfferCreated -> signalingClient.sendSDPMessage(event.sdp)
            is PeerConnectionEvents.AnswerCreated -> signalingClient.sendSDPMessage(event.sdp)
            is PeerConnectionEvents.IceCandidateCreated -> signalingClient.sendIceCandidate(event.iceCandidate)
            is PeerConnectionEvents.PeerStreamConnected -> streamEvents.emit(StreamEvent.CONNECTED)
            is PeerConnectionEvents.PeerStreamDisconnected -> {
                cleanup()
                streamEvents.emit(StreamEvent.NOT_STARTED)
            }
        }
    }

    private fun cleanup() {
        signalingClient.disconnect()
        peerConnectionClient.disconnect()
    }

    override fun getRoomConnectionEventFlow(): Flow<SocketRoomConnectionEvents> = signalingClient.getSocketRoomEventFlow()

    override fun getStreamEventFlow(): Flow<StreamEvent> = streamEvents

    private fun getSocketMessageEventFlow() = signalingClient.getSocketMessageEventFlow()

    private fun getPeerConnectionEventFlow() = peerConnectionClient.getPeerConnectionEventFlow()
}