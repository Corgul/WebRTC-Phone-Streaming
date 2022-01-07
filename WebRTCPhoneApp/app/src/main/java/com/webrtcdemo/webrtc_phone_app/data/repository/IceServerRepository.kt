package com.webrtcdemo.webrtc_phone_app.data.repository

import com.webrtcdemo.webrtc_phone_app.data.remote.dto.IceServerDto

interface IceServerRepository {
    /**
     * Gets the data transfer object representing the list of ice servers
     */
    suspend fun getIceServers(): IceServerDto
}