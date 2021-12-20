package com.webrtcdemo.webrtc_phone_app.webrtc

import kotlinx.coroutines.flow.Flow
import org.webrtc.EglBase
import org.webrtc.IceCandidate
import org.webrtc.SessionDescription
import org.webrtc.VideoSink

interface PeerConnectionClient {
    fun setupPeerConnection(eglBase: EglBase)

    fun disconnect()

    fun initVideoSink(videoSink: VideoSink)

    fun unbindVideoSink()

    fun createOffer()

    fun onOfferReceived(offer: SessionDescription)

    fun createAnswer()

    fun onAnswerReceived(answer: SessionDescription)

    fun onIceCandidateReceived(iceCandidate: IceCandidate)

    fun getPeerConnectionEventFlow(): Flow<PeerConnectionEvents?>
}