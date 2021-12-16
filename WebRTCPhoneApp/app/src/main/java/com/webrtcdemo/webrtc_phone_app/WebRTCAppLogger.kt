package com.webrtcdemo.webrtc_phone_app

import android.util.Log

object WebRTCAppLogger {
    fun d(message: String) {
        val stElements = Thread.currentThread().stackTrace
        Log.d(getTag(), message)
    }

    private fun getTag(): String? {
        var tag = ""
        val ste = Thread.currentThread().stackTrace
        for (i in ste.indices) {
            if (ste[i].methodName == "d") {
                tag = "(" + ste[i + 1].fileName + ":" + ste[i + 1].lineNumber + ")"
            }
        }
        return tag
    }

}