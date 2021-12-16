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


@Singleton
class SignalingClientImpl : SignalingClient {
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
        IO.setDefaultHostnameVerifier() { _: String, _: SSLSession ->
            true
        }
        IO.setDefaultSSLContext(sslContext)
        socket = IO.socket("http:127.0.0.1:8080")
    }

    override fun connect(roomName: String) {
        socket.connect()
        if (roomName.isNotEmpty()) {
            socket.emit(CREATE_OR_JOIN, roomName)
        }

        setupCallbacks()
    }

    private fun setupCallbacks() {
        // log event
        socket.on("log") { args: Array<Any> ->
            WebRTCAppLogger.d("log call() called with: args = [" + args.contentToString() + "]")
        }
    }

    companion object {
        const val CREATE_OR_JOIN = "create or join"
    }
}