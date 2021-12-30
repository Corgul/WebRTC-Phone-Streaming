package com.webrtcdemo.webrtc_phone_app.data.remote.dto

import com.google.gson.annotations.SerializedName
import org.webrtc.PeerConnection

data class IceServerDetailDto(
    @SerializedName("url")
    val url: String,
    @SerializedName("username")
    val username: String?,
    @SerializedName("credential")
    val credential: String?
)

fun IceServerDetailDto.toIceServer(): PeerConnection.IceServer {
    if (this.credential == null) {
        return PeerConnection.IceServer.builder(this.url).createIceServer()
    }
    return PeerConnection.IceServer.builder(this.url)
        .setUsername(this.username)
        .setPassword(this.credential)
        .createIceServer()
}