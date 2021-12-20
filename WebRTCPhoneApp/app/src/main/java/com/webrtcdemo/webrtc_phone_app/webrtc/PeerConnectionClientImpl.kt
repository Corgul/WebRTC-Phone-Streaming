package com.webrtcdemo.webrtc_phone_app.webrtc

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import org.webrtc.*
import javax.inject.Inject

@ViewModelScoped
class PeerConnectionClientImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : PeerConnectionClient {
    private var peerConnectionFactory: PeerConnectionFactory? = null
    private var peerConnection: PeerConnection? = null
    private val peerConnectionEvents = MutableStateFlow<PeerConnectionEvents?>(null)
    private val videoSink = ProxyVideoSink()

    override fun initVideoSink(videoSink: VideoSink) {
        this.videoSink.setTargetVideoSink(videoSink)
    }

    override fun unbindVideoSink() {
        videoSink.unbindTargetVideoSink()
    }

    override fun setupPeerConnection(eglBase: EglBase) {
        setupPeerConnectionFactory(eglBase)
    }

    private fun setupPeerConnectionFactory(eglBase: EglBase) {
        val initializationOptions = PeerConnectionFactory.InitializationOptions
            .builder(context)
            .createInitializationOptions()
        PeerConnectionFactory.initialize(initializationOptions)

        val options = PeerConnectionFactory.Options()
        val defaultVideoEncoderFactory = DefaultVideoEncoderFactory(eglBase.eglBaseContext, true, true)
        val defaultVideoDecoderFactory = DefaultVideoDecoderFactory(eglBase.eglBaseContext)

        peerConnectionFactory = PeerConnectionFactory.builder()
            .setOptions(options)
            .setVideoEncoderFactory(defaultVideoEncoderFactory)
            .setVideoDecoderFactory(defaultVideoDecoderFactory)
            .createPeerConnectionFactory()

        setupPeerConnection()
    }

    private fun setupPeerConnection() {
        // TODO - Use xirsys for TURN and STUN Server
        val rtcConfig = PeerConnection.RTCConfiguration(listOf())

        rtcConfig.bundlePolicy = PeerConnection.BundlePolicy.MAXBUNDLE
        rtcConfig.sdpSemantics = PeerConnection.SdpSemantics.UNIFIED_PLAN

        peerConnection = peerConnectionFactory?.createPeerConnection(rtcConfig, object : CustomPeerConnectionObserver() {
            override fun onIceCandidate(iceCandidate: IceCandidate?) {
                super.onIceCandidate(iceCandidate)
                onIceCandidateCreated(iceCandidate)
            }

            override fun onAddStream(stream: MediaStream) {
                super.onAddStream(stream)
                addRemoteStream(stream)
            }
        })

    }

    private fun onIceCandidateCreated(iceCandidate: IceCandidate?) {
        if (iceCandidate == null) {
            return
        }
        peerConnectionEvents.value = PeerConnectionEvents.IceCandidateCreated(iceCandidate)
    }

    private fun addRemoteStream(stream: MediaStream) {
        val videoTrack = stream.videoTracks.first()
        videoTrack.addSink(videoSink)
    }

    override fun disconnect() {
        peerConnection?.dispose()
        peerConnection = null
        peerConnectionFactory?.dispose()
        peerConnectionFactory = null
    }

    override fun createOffer() {
        val sdpConstraints = MediaConstraints()
        sdpConstraints.mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"))
        sdpConstraints.mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"))
        peerConnection?.let { peerConnection ->
            peerConnection.createOffer(object : CustomSdpObserver() {
                override fun onCreateSuccess(sdp: SessionDescription) {
                    super.onCreateSuccess(sdp)
                    peerConnection.setLocalDescription(CustomSdpObserver(), sdp)
                    peerConnectionEvents.value = PeerConnectionEvents.OfferCreated(sdp)
                }
            }, sdpConstraints)
        }
    }

    override fun createAnswer() {
        peerConnection?.let { peerConnection ->
            peerConnection.createAnswer(object : CustomSdpObserver() {
                override fun onCreateSuccess(sdp: SessionDescription) {
                    super.onCreateSuccess(sdp)
                    peerConnection.setLocalDescription(CustomSdpObserver(), sdp)
                    peerConnectionEvents.value = PeerConnectionEvents.AnswerCreated(sdp)
                }
            }, MediaConstraints())
        }
    }

    override fun onOfferReceived(offer: SessionDescription) {
        peerConnection?.setRemoteDescription(CustomSdpObserver(), offer)
    }

    override fun onAnswerReceived(answer: SessionDescription) {
        peerConnection?.setRemoteDescription(CustomSdpObserver(), answer)
    }

    override fun onIceCandidateReceived(iceCandidate: IceCandidate) {
        peerConnection?.addIceCandidate(iceCandidate)
    }

    override fun getPeerConnectionEventFlow(): Flow<PeerConnectionEvents?> = peerConnectionEvents
}