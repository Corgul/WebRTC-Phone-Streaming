package com.webrtcdemo.webrtc_phone_app.webrtc

import org.webrtc.IceCandidate
import org.webrtc.SessionDescription

sealed class PeerConnectionEvents {
    class OfferCreated(val sdp: SessionDescription) : PeerConnectionEvents()
    class AnswerCreated(val sdp: SessionDescription) : PeerConnectionEvents()
    class IceCandidateCreated(val iceCandidate: IceCandidate) : PeerConnectionEvents()
    object PeerStreamConnected : PeerConnectionEvents()
    object PeerStreamDisconnected: PeerConnectionEvents()
}