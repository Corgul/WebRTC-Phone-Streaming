package com.webrtcdemo.webrtc_phone_app.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.webrtcdemo.webrtc_phone_app.WebRTCAppLogger
import com.webrtcdemo.webrtc_phone_app.webrtc.SocketRoomEvent
import com.webrtcdemo.webrtc_phone_app.webrtc.StreamEvent
import com.webrtcdemo.webrtc_phone_app.webrtc.WebRTCStreamReceiver
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewCameraViewModel @Inject constructor(
    private val webRTCStreamReceiver: WebRTCStreamReceiver
) : ViewModel() {
    val roomName = MutableLiveData<String>()

    init {
        viewModelScope.launch {
            webRTCStreamReceiver.getRoomEventFlow()
                .filter { it != null }
                .onEach { onRoomEvent(it) }
                .launchIn(viewModelScope)
            webRTCStreamReceiver.getStreamEventsFlow()
                .filter { it != null }
                .onEach { onStreamEvent(it) }
                .launchIn(viewModelScope)
        }
    }

    private fun onStreamEvent(streamEvent: StreamEvent?) {
        if (streamEvent == null) {
            return
        }
        WebRTCAppLogger.d("onStreamEvent: $streamEvent")
    }

    private fun onRoomEvent(socketRoomEvent: SocketRoomEvent?) {
        if (socketRoomEvent == null) {
            return
        }
        WebRTCAppLogger.d("onRoomEvent: $socketRoomEvent")
    }

    fun onJoinRoomClicked() {
        val room = roomName.value
        if (room.isNullOrEmpty()) {
            return
        }
        webRTCStreamReceiver.connectToRoom(room)
        WebRTCAppLogger.d("Join room clicked + $room")
    }
}