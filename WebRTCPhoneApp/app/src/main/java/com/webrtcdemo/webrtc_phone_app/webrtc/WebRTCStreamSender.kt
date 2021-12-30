package com.webrtcdemo.webrtc_phone_app.webrtc

import com.webrtcdemo.webrtc_phone_app.WebRTCAppLogger
import com.webrtcdemo.webrtc_phone_app.common.Resource
import com.webrtcdemo.webrtc_phone_app.signaling.SignalingClient
import com.webrtcdemo.webrtc_phone_app.use_case.GetIceServersUseCase
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@ViewModelScoped
class WebRTCStreamSender @Inject constructor(
    signalingClient: SignalingClient,
    private val peerConnectionClient: PeerConnectionClient,
    private val viewModelScope: CoroutineScope,
    private val getIceServersUseCase: GetIceServersUseCase
) : BaseWebRTCStreamImpl(signalingClient, peerConnectionClient, viewModelScope) {

    override fun setupPeerConnection() {
        viewModelScope.launch {
            getIceServersUseCase().collect { result ->
                when (result) {
                    is Resource.Success -> peerConnectionClient.setupPeerConnection(EglBaseWrapper.eglBase, result.data)
                    is Resource.Error -> {
                        WebRTCAppLogger.d("Error received from API: ${result.message}")
                        peerConnectionClient.setupPeerConnection(EglBaseWrapper.eglBase)
                    }
                }
                peerConnectionClient.setupCameraStreamingSupport(EglBaseWrapper.eglBase)
            }
        }
    }
}