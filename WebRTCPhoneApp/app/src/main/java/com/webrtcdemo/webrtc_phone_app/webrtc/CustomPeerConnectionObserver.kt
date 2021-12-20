package com.webrtcdemo.webrtc_phone_app.webrtc

import org.webrtc.*

open class CustomPeerConnectionObserver : PeerConnection.Observer {
    override fun onSignalingChange(state: PeerConnection.SignalingState?) {

    }

    override fun onIceConnectionChange(state: PeerConnection.IceConnectionState?) {

    }

    override fun onStandardizedIceConnectionChange(newState: PeerConnection.IceConnectionState?) {
        super.onStandardizedIceConnectionChange(newState)
    }

    override fun onConnectionChange(newState: PeerConnection.PeerConnectionState?) {
        super.onConnectionChange(newState)
    }

    override fun onIceConnectionReceivingChange(receiving: Boolean) {
    }

    override fun onIceGatheringChange(state: PeerConnection.IceGatheringState?) {
    }

    override fun onIceCandidate(iceCandidate: IceCandidate?) {
    }

    override fun onIceCandidatesRemoved(p0: Array<out IceCandidate>?) {
    }

    override fun onAddStream(stream: MediaStream) {
    }

    override fun onRemoveStream(stream: MediaStream?) {
    }

    override fun onDataChannel(dataChannel: DataChannel?) {
    }

    override fun onRenegotiationNeeded() {
    }

    override fun onAddTrack(receiver: RtpReceiver?, mediaStreams: Array<out MediaStream>?) {
    }

    override fun onTrack(transceiver: RtpTransceiver?) {
        super.onTrack(transceiver)
    }
}