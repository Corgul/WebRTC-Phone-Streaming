package com.webrtcdemo.webrtc_phone_app.webrtc

import com.webrtcdemo.webrtc_phone_app.WebRTCAppLogger
import org.webrtc.SdpObserver
import org.webrtc.SessionDescription

open class CustomSdpObserver : SdpObserver {
    override fun onCreateSuccess(sdp: SessionDescription) {
        WebRTCAppLogger.d("onCreateSuccess")
    }

    override fun onSetSuccess() {
        WebRTCAppLogger.d("onSetSuccess")
    }

    override fun onCreateFailure(message: String) {
        WebRTCAppLogger.d("onCreateFailure: $message")
    }

    override fun onSetFailure(message: String) {
        WebRTCAppLogger.d("onSetFailure $message")
    }
}