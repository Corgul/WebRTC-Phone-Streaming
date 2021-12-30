package com.webrtcdemo.webrtc_phone_app.data.repository

import com.webrtcdemo.webrtc_phone_app.data.remote.dto.IceServerDto

interface IceServerRepository {
    suspend fun getIceServers(): IceServerDto
}