package com.webrtcdemo.webrtc_phone_app.webrtc

import org.json.JSONObject

sealed class SocketMessageEvents {
    class OfferReceived(val data: JSONObject) : SocketMessageEvents()
    class AnswerReceived(val data: JSONObject) : SocketMessageEvents()
    class IceCandidateReceived(val data: JSONObject) : SocketMessageEvents()
}