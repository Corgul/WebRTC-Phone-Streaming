package com.webrtcdemo.webrtc_phone_app.webrtc

import android.content.Context
import com.webrtcdemo.webrtc_phone_app.WebRTCAppLogger
import com.webrtcdemo.webrtc_phone_app.di.ViewModelCoroutineScope
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import org.webrtc.*
import javax.inject.Inject
import org.webrtc.VideoCapturer

@ViewModelScoped
class PeerConnectionClientImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    @ViewModelCoroutineScope private val viewModelScope: CoroutineScope
) : PeerConnectionClient {
    private var peerConnectionFactory: PeerConnectionFactory? = null
    private var peerConnection: PeerConnection? = null
    private val peerConnectionEvents = MutableSharedFlow<PeerConnectionEvents>()
    private val videoSink = ProxyVideoSink()

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
        // Default initialization options for the PeerConnectionFactory
        val initializationOptions = PeerConnectionFactory.InitializationOptions
            .builder(context)
            .createInitializationOptions()
        PeerConnectionFactory.initialize(initializationOptions)

        // Default options and video encoder/decoder for the PeerConnectionFactory
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
        // No ICE Servers (STUN/TURN) passed in yet, just use the default STUN Servers the library uses
        val rtcConfig = PeerConnection.RTCConfiguration(listOf())

        // Gather ICE Candidates for each media track. If set to "BALANCED" will gather ice candidates for each media type (audio/video)
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
        // Uses the helper class VideoCapturer to capture the camera feed
        val videoCapturer = createCameraCapturer(Camera1Enumerator(false)) ?: return
        // Uses a SurfaceTexture to help create the WebRTC video frames
        val surfaceTextureHelper = SurfaceTextureHelper.create("CaptureThread", eglBase.eglBaseContext)
        val videoSource = peerConnectionFactory?.createVideoSource(videoCapturer.isScreencast)

        // Initializes to render camera frames on to the SurfaceTextureHelper, register itself as a listener, and forward frames to the captureObserver
        videoCapturer.initialize(surfaceTextureHelper, context, videoSource?.capturerObserver)
        // Records camera at 1024/720 at 30 FPS
        videoCapturer.startCapture(1024, 720, 30)

        createLocalVideoStream(videoSource)
    }

    private fun createLocalVideoStream(videoSource: VideoSource?) {
        // Create video track with arbitrary id
        val localVideoTrack = peerConnectionFactory?.createVideoTrack("100", videoSource)
        localVideoTrack?.setEnabled(true)
        // Allows us to see our own camera stream
        localVideoTrack?.addSink(videoSink)

        // Create MediaStream with arbitrary id
        val stream = peerConnectionFactory?.createLocalMediaStream("101")
        stream?.addTrack(localVideoTrack)
        peerConnection?.addStream(stream)
    }

    private fun onIceCandidateCreated(iceCandidate: IceCandidate?) {
        if (iceCandidate == null) {
            return
        }
        viewModelScope.launch { peerConnectionEvents.emit(PeerConnectionEvents.IceCandidateCreated(iceCandidate)) }
    }

    private fun addRemoteStream(stream: MediaStream) {
        viewModelScope.launch {
            WebRTCAppLogger.d("Added stream")
            val videoTrack = stream.videoTracks.first()
            // Adds the sink to the video track, causing all frames from the track to be fed into it
            videoTrack.addSink(videoSink)
            peerConnectionEvents.emit(PeerConnectionEvents.PeerStreamConnected)
        }
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
                    viewModelScope.launch {
                        super.onCreateSuccess(sdp)
                        peerConnection.setLocalDescription(CustomSdpObserver(), sdp)
                        peerConnectionEvents.emit(PeerConnectionEvents.OfferCreated(sdp))
                    }
                }
            }, sdpConstraints)
        }
    }

    override fun createAnswer() {
        peerConnection?.let { peerConnection ->
            peerConnection.createAnswer(object : CustomSdpObserver() {
                override fun onCreateSuccess(sdp: SessionDescription) {
                    viewModelScope.launch {
                        super.onCreateSuccess(sdp)
                        WebRTCAppLogger.d("Created answer")
                        peerConnection.setLocalDescription(CustomSdpObserver(), sdp)
                        peerConnectionEvents.emit(PeerConnectionEvents.AnswerCreated(sdp))
                    }
                }
            }, MediaConstraints())
        }
    }

    override suspend fun onOfferReceived(offer: SessionDescription) {
        WebRTCAppLogger.d("Offer received")
        peerConnection?.setRemoteDescription(CustomSdpObserver(), offer)
        peerConnectionEvents.emit(PeerConnectionEvents.PeerStreamConnected)
        createAnswer()
    }

    override fun onAnswerReceived(answer: SessionDescription) {
        WebRTCAppLogger.d("Answer received")
        peerConnection?.setRemoteDescription(CustomSdpObserver(), answer)
    }

    override fun onIceCandidateReceived(iceCandidate: IceCandidate) {
        WebRTCAppLogger.d("Ice Candidate received")
        peerConnection?.addIceCandidate(iceCandidate)
    }

    private fun createCameraCapturer(enumerator: CameraEnumerator): VideoCapturer? {
        val deviceNames = enumerator.deviceNames

        for (deviceName in deviceNames) {
            if (enumerator.isBackFacing(deviceName)) {
                WebRTCAppLogger.d("Creating front facing camera capturer.")
                return createVideoCapturerHelper(enumerator, deviceName)
            } else if (enumerator.isFrontFacing(deviceName)) {
                WebRTCAppLogger.d("Creating back facing camera capturer.")
                return createVideoCapturerHelper(enumerator, deviceName)
            }
        }

        return null
    }

    private fun createVideoCapturerHelper(enumerator: CameraEnumerator, deviceName: String): VideoCapturer? {
        return enumerator.createCapturer(deviceName, null)
    }

    override fun getPeerConnectionEventFlow(): Flow<PeerConnectionEvents> = peerConnectionEvents
}