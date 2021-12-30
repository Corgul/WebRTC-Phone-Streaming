package com.webrtcdemo.webrtc_phone_app.data.remote

import com.webrtcdemo.webrtc_phone_app.BuildConfig
import com.webrtcdemo.webrtc_phone_app.data.remote.dto.IceServerDto
import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.PUT

interface IceServerApi {
    @PUT("/_turn/${BuildConfig.XIRSYS_CHANNEL}")
    suspend fun getIceServers(@Header("Authorization") authKey: String): IceServerDto
}