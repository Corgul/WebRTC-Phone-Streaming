package com.webrtcdemo.webrtc_phone_app.viewmodel

import androidx.lifecycle.*
import com.webrtcdemo.webrtc_phone_app.WebRTCAppLogger
import com.webrtcdemo.webrtc_phone_app.di.WebRTCStreamReceiverQualifier
import com.webrtcdemo.webrtc_phone_app.webrtc.SocketRoomConnectionEvents
import com.webrtcdemo.webrtc_phone_app.webrtc.BaseWebRTCStream
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.webrtc.VideoSink
import javax.inject.Inject

@HiltViewModel
class ViewCameraViewModel @Inject constructor(
    @WebRTCStreamReceiverQualifier private val webRTCStream: BaseWebRTCStream
) : ViewModel() {
    val roomName = MutableLiveData<String>()
    val loadingText = MutableLiveData<String>()
    val streamEvents = webRTCStream.getStreamEventFlow().asLiveData()

    init {
        viewModelScope.launch {
            webRTCStream.getRoomConnectionEventFlow()
                .collect { onRoomEvent(it) }
        }
    }

    fun initVideoSink(videoSink: VideoSink) {
        webRTCStream.initVideoSink(videoSink)
    }

    fun unbindVideoSink() {
        webRTCStream.unbindVideoSink()
    }

    private fun onRoomEvent(socketRoomConnectionEvent: SocketRoomConnectionEvents) {
        when (socketRoomConnectionEvent) {
            SocketRoomConnectionEvents.CONNECTING -> connectingToRoom()
            SocketRoomConnectionEvents.CONNECTED -> connectedToRoom()
            SocketRoomConnectionEvents.CREATED_ROOM -> createdRoom()
            SocketRoomConnectionEvents.JOINED_EXISTING_ROOM -> joinedExistingRoom()
            SocketRoomConnectionEvents.PEER_JOINED_ROOM -> peerJoinedExistingRoom()
        }
        WebRTCAppLogger.d("onRoomConnectionEvent: $socketRoomConnectionEvent")
    }

    private fun connectingToRoom() {
        loadingText.value = "Connecting to the room"
    }

    private fun connectedToRoom() {
        loadingText.value = "Connected to the room"
    }

    private fun createdRoom() {
        loadingText.value = "Created a room"
    }

    private fun joinedExistingRoom() {
        loadingText.value = "Joined an existing room"
    }

    private fun peerJoinedExistingRoom() {
        loadingText.value = "Peer joined our room"
    }

    fun onJoinRoomClicked() {
        viewModelScope.launch {
            val room = roomName.value
            if (room.isNullOrEmpty()) {
                return@launch
            }
            webRTCStream.connectToRoom(room)
            WebRTCAppLogger.d("Join room clicked + $room")
        }
    }
}