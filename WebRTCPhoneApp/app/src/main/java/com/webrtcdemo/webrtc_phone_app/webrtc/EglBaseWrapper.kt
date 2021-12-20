package com.webrtcdemo.webrtc_phone_app.webrtc

import org.webrtc.EglBase

// Singleton
object EglBaseWrapper {
    val eglBase: EglBase by lazy { EglBase.create() }
}