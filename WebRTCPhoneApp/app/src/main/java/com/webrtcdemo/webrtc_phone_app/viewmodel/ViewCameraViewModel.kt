package com.webrtcdemo.webrtc_phone_app.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.webrtcdemo.webrtc_phone_app.WebRTCAppLogger
import com.webrtcdemo.webrtc_phone_app.webrtc.WebRTCStreamReceiver
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ViewCameraViewModel @Inject constructor(
    private val webRTCStreamReceiver: WebRTCStreamReceiver
) : ViewModel() {
    val roomName = MutableLiveData<String>()

    fun onJoinRoomClicked() {
        val room = roomName.value
        if (room.isNullOrEmpty()) {
            return
        }
        WebRTCAppLogger.d("Join room clicked + $room")
    }
}