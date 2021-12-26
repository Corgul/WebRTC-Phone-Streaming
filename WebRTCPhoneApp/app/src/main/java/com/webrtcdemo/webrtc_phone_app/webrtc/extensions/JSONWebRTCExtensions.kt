package com.webrtcdemo.webrtc_phone_app.webrtc.extensions

import org.json.JSONObject
import org.webrtc.IceCandidate
import org.webrtc.SessionDescription

fun IceCandidate.toJson(): JSONObject {
    val json = JSONObject()
    json.put("type", "candidate")
    json.put("label", this.sdpMLineIndex)
    json.put("id", this.sdpMid)
    json.put("candidate", this.sdp)
    return json
}

fun SessionDescription.toJson(): JSONObject {
    val json = JSONObject()
    json.put("type", this.type.canonicalForm())
    json.put("sdp", this.description)
    return json
}

fun JSONObject.toIceCandidate(): IceCandidate {
    return IceCandidate(
        this.getString("id"),
        this.getInt("label"),
        this.getString("candidate")
    )
}

fun JSONObject.toSessionDescription(): SessionDescription {
    return SessionDescription(
        SessionDescription.Type.fromCanonicalForm(this.getString("type").lowercase()),
        this.getString("sdp")
    )
}
