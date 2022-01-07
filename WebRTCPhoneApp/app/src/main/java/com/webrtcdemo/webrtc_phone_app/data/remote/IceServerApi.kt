package com.webrtcdemo.webrtc_phone_app.data.remote

import com.webrtcdemo.webrtc_phone_app.BuildConfig
import com.webrtcdemo.webrtc_phone_app.data.remote.dto.IceServerDto
import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.PUT

interface IceServerApi {
    // Documentation - https://docs.xirsys.com/?pg=api-turn. This API endpoint gets a list of Ice Servers (STUN/TURN) from Xirsys
    // Marking this function as suspend automatically runs on Dispatchers.IO with retrofit2
    @PUT("/_turn/${BuildConfig.XIRSYS_CHANNEL}")
    suspend fun getIceServers(@Header("Authorization") authKey: String): IceServerDto
}