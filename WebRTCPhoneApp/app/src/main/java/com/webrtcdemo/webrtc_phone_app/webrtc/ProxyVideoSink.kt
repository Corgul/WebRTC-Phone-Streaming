package com.webrtcdemo.webrtc_phone_app.webrtc

import com.webrtcdemo.webrtc_phone_app.WebRTCAppLogger
import org.webrtc.VideoFrame
import org.webrtc.VideoSink

class ProxyVideoSink : VideoSink {
    private var targetVideoSink: VideoSink? = null

    @Synchronized
    override fun onFrame(frame: VideoFrame) {
        if (targetVideoSink == null) {
            WebRTCAppLogger.d("Target is null, dropping frame")
        }
        targetVideoSink?.onFrame(frame)
    }

    @Synchronized
    fun setTargetVideoSink(videoSink: VideoSink) {
        targetVideoSink = videoSink
    }

    @Synchronized
    fun unbindTargetVideoSink() {
        targetVideoSink = null
    }
}