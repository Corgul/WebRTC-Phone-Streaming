package com.webrtcdemo.webrtc_phone_app.webrtc

import org.webrtc.EglBase

/**
 * Singleton
 *
 * Android uses the OpenGL ES (GLES) API to render graphics. To create GLES contexts and provide a windowing system for GLES renderings,
 * Android uses the EGL library.
 */
object EglBaseWrapper {
    /**
     * EglBase just holds EGL state and utility methods for 
     */
    val eglBase: EglBase by lazy { EglBase.create() }
}