package com.webrtcdemo.webrtc_phone_app.repository

interface IceServerRepository {
    suspend fun getIceServers()
}