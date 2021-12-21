package com.webrtcdemo.webrtc_phone_app.webrtc

import android.content.Context
import com.webrtcdemo.webrtc_phone_app.WebRTCAppLogger
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import org.webrtc.*
import javax.inject.Inject
import org.webrtc.VideoCapturer

@ViewModelScoped
class PeerConnectionClientImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : PeerConnectionClient {
    private var peerConnectionFactory: PeerConnectionFactory? = null
    private var peerConnection: PeerConnection? = null
    private val peerConnectionEvents = MutableStateFlow<PeerConnectionEvents?>(null)
    private val videoSink = ProxyVideoSink()
    private var surfaceTextureHelper: SurfaceTextureHelper? = null

    override fun initVideoSink(videoSink: VideoSink) {
        WebRTCAppLogger.d("initVideoSink")
        this.videoSink.setTargetVideoSink(videoSink)
    }

    override fun unbindVideoSink() {
        WebRTCAppLogger.d("unbindVideoSink")
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

    override fun setupCameraStreamingSupport(eglBase: EglBase) {
        val videoCapturer = createCameraCapturer(Camera1Enumerator(false))

        if (videoCapturer != null) {
            surfaceTextureHelper = SurfaceTextureHelper.create("CaptureThread", eglBase.eglBaseContext)
            val videoSource = peerConnectionFactory?.createVideoSource(videoCapturer.isScreencast)
            videoCapturer.initialize(surfaceTextureHelper, context, videoSource?.capturerObserver)
            videoCapturer.startCapture(1024, 720, 30)
            val localVideoTrack = peerConnectionFactory?.createVideoTrack("100", videoSource)
            localVideoTrack?.setEnabled(true)
            localVideoTrack?.addSink(videoSink)
            val stream = peerConnectionFactory?.createLocalMediaStream("101")
            stream?.addTrack(localVideoTrack)
            peerConnection?.addStream(stream)
        }
    }

    private fun onIceCandidateCreated(iceCandidate: IceCandidate?) {
        if (iceCandidate == null) {
            return
        }
        Thread.sleep(1000)
        peerConnectionEvents.value = PeerConnectionEvents.IceCandidateCreated(iceCandidate)
    }

    private fun addRemoteStream(stream: MediaStream) {
        WebRTCAppLogger.d("Added stream")
        val videoTrack = stream.videoTracks.first()
        videoTrack.addSink(videoSink)
    }

    override fun disconnect() {
        WebRTCAppLogger.d("Disconnecting peer connection")
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
        Thread.sleep(1000)
        peerConnection?.let { peerConnection ->
            peerConnection.createAnswer(object : CustomSdpObserver() {
                override fun onCreateSuccess(sdp: SessionDescription) {
                    super.onCreateSuccess(sdp)
                    WebRTCAppLogger.d("Created answer")
                    peerConnection.setLocalDescription(CustomSdpObserver(), sdp)
                    peerConnectionEvents.value = PeerConnectionEvents.AnswerCreated(sdp)
                }
            }, MediaConstraints())
        }
    }

    override fun onOfferReceived(offer: SessionDescription) {
        WebRTCAppLogger.d("Offer received")
        peerConnection?.setRemoteDescription(CustomSdpObserver(), offer)
        peerConnectionEvents.value = PeerConnectionEvents.PeerStreamConnected
        createAnswer()
    }

    override fun onAnswerReceived(answer: SessionDescription) {
        WebRTCAppLogger.d("Answer received")
        peerConnection?.setRemoteDescription(CustomSdpObserver(), answer)
        peerConnectionEvents.value = PeerConnectionEvents.PeerStreamConnected
    }

    override fun onIceCandidateReceived(iceCandidate: IceCandidate) {
        WebRTCAppLogger.d("Ice Candidate received")
        peerConnection?.addIceCandidate(iceCandidate)
    }

    private fun createCameraCapturer(enumerator: CameraEnumerator): VideoCapturer? {
        val deviceNames = enumerator.deviceNames

        WebRTCAppLogger.d("Looking for back facing cameras.")
        for (deviceName in deviceNames) {
            if (enumerator.isBackFacing(deviceName)) {
                WebRTCAppLogger.d("Creating front facing camera capturer.")
                val videoCapturer: VideoCapturer? = enumerator.createCapturer(deviceName, null)
                if (videoCapturer != null) {
                    return videoCapturer
                }
            }
        }

        WebRTCAppLogger.d("Looking for front cameras.")
        for (deviceName in deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
                WebRTCAppLogger.d("Creating other camera capturer.")
                val videoCapturer: VideoCapturer? = enumerator.createCapturer(deviceName, null)
                if (videoCapturer != null) {
                    return videoCapturer
                }
            }
        }

        return null
    }

    override fun getPeerConnectionEventFlow(): Flow<PeerConnectionEvents?> = peerConnectionEvents
}