package com.webrtcdemo.webrtc_phone_app.signaling

import android.annotation.SuppressLint
import io.socket.client.IO
import io.socket.client.Socket
import java.security.cert.X509Certificate
import javax.inject.Singleton
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSession
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import java.util.Arrays

import android.util.Log
import com.webrtcdemo.webrtc_phone_app.WebRTCAppLogger
import com.webrtcdemo.webrtc_phone_app.webrtc.SocketRoomEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import io.socket.emitter.Emitter

import io.socket.engineio.client.Transport

import io.socket.client.Manager





@Singleton
class SignalingClientImpl @Inject constructor() : SignalingClient {
    private val roomEvents = MutableStateFlow<SocketRoomEvent?>(null)
    private val socket: Socket

    // Should not be in production
    private val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
        @SuppressLint("TrustAllX509TrustManager")
        override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {
        }

        @SuppressLint("TrustAllX509TrustManager")
        override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {
        }

        override fun getAcceptedIssuers(): Array<X509Certificate> {
            return arrayOf()
        }
    })

    init {
        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, trustAllCerts, null)
//        IO.setDefaultHostnameVerifier() { _: String, _: SSLSession ->
//            true
//        }
//        IO.setDefaultSSLContext(sslContext)
        socket = IO.socket("http://192.168.0.162:8080")
    }

    override fun connect(roomName: String) {
        roomEvents.value = SocketRoomEvent.CONNECTING
        socket.connect()
        if (roomName.isNotEmpty()) {
            socket.emit(CREATE_OR_JOIN, roomName)
        }

        setupCallbacks()
    }

    private fun setupCallbacks() {
        socket.on(Socket.EVENT_DISCONNECT) {
            WebRTCAppLogger.d("Disconnected ${it.contentToString()}")
        }
        socket.on(Manager.EVENT_ERROR) {
            WebRTCAppLogger.d("error")
        }
        // log event
        socket.on("log") { args: Array<Any> ->
            WebRTCAppLogger.d("log call() called with: args = [" + args.contentToString() + "]")
        }
        socket.on("created") {
            WebRTCAppLogger.d("onRoomEvent2 created, ${it.contentToString()}")
            roomEvents.value = SocketRoomEvent.CREATED_ROOM
        }
        socket.on("joined") {
            WebRTCAppLogger.d("onRoomEvent2 joined, ${it.contentToString()}")
            roomEvents.value = SocketRoomEvent.JOINED_EXISTING_ROOM
        }
        socket.on("join") {
            WebRTCAppLogger.d("onRoomEvent2 join, ${it.contentToString()}")
            roomEvents.value = SocketRoomEvent.PEER_JOINED_ROOM
        }
        socket.on("full") {
            WebRTCAppLogger.d("onRoomEvent2 full, ${it.contentToString()}")
            roomEvents.value = SocketRoomEvent.ROOM_IS_FULL
        }
        socket.on("ready") {
            WebRTCAppLogger.d("onRoomEvent2 ready, ${it.contentToString()}")
        }
    }

    override fun getSocketRoomEventFlow(): Flow<SocketRoomEvent?> = roomEvents

    companion object {
        const val CREATE_OR_JOIN = "create or join"
    }
}