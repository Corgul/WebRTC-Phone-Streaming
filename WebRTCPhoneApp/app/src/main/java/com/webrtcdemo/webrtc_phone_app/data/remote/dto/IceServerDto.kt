package com.webrtcdemo.webrtc_phone_app.data.remote.dto

import com.google.gson.annotations.SerializedName
import org.webrtc.PeerConnection

/**
 * Data transfer object encapsulating the data returned from the API class IceServerApi
 */
data class IceServerDto(
    @SerializedName("s")
    val status: String,
    @SerializedName("v")
    val iceServerDetailList: IceServerDetailListDto
) {
    data class IceServerDetailListDto(
        @SerializedName("iceServers")
        val list: List<IceServerDetailDto>? = null
    )
}

fun IceServerDto.IceServerDetailListDto.toIceServerList(): List<PeerConnection.IceServer>? {
    if (this.list == null) {
        return null
    }
    return this.list.map { it.toIceServer() }
}