package com.webrtcdemo.webrtc_phone_app.webrtc

import kotlinx.coroutines.flow.Flow
import org.webrtc.*

interface PeerConnectionClient {
    /**
     * Sets up the peer connection objects
     * @param eglBase the [EglBase] to use for video decoding/encoding
     */
    fun setupPeerConnection(eglBase: EglBase)

    /**
     * Sets up the peer connection objects
     * @param eglBase the [EglBase] to use for video decoding/encoding
     * @param iceServers the list of ice servers (STUN/TURN)
     */
    fun setupPeerConnection(eglBase: EglBase, iceServers: List<PeerConnection.IceServer>?)

    /**
     * Sets up streaming support from the phone's camera
     */
    fun setupCameraStreamingSupport(eglBase: EglBase)

    /**
     * Disconnects from the peer and cleans up any resources
     */
    fun disconnect()

    /**
     * Initializes a video sink to start feeding frames into. This is normally the SurfaceViewRenderer
     */
    fun initVideoSink(videoSink: VideoSink)

    /**
     * Unbinds the video sink from the stream's video track
     */
    fun unbindVideoSink()

    /**
     * Creates a [SessionDescription] offer object to send to a peer
     */
    fun createOffer()

    /**
     * Called when we receive an [SessionDescription] offer and sets it as the remote description
     */
    suspend fun onOfferReceived(offer: SessionDescription)

    /**
     * Creates a [SessionDescription] answer object to send to a peer
     */
    fun createAnswer()

    /**
     * Called when we receive an [SessionDescription] offer and sets it as the remote description
     */
    fun onAnswerReceived(answer: SessionDescription)

    /**
     * Adds ice candidates to the WebRTC Library
     */
    fun onIceCandidateReceived(iceCandidate: IceCandidate)

    /**
     * Gets the flow of events related to PeerConnection connection status and objects being created such as SDP offer/answer and Ice Candidates
     */
    fun getPeerConnectionEventFlow(): Flow<PeerConnectionEvents>
}