package com.webrtcdemo.webrtc_phone_app.data.repository

import android.util.Base64
import com.webrtcdemo.webrtc_phone_app.BuildConfig
import com.webrtcdemo.webrtc_phone_app.data.remote.IceServerApi
import com.webrtcdemo.webrtc_phone_app.data.remote.dto.IceServerDto
import javax.inject.Inject

class IceServerRepositoryImpl @Inject constructor(
    private val api: IceServerApi
) : IceServerRepository {
    override suspend fun getIceServers(): IceServerDto {
        // toByteArray defaults to UTF-8 encoding
        val data = "${BuildConfig.XIRSYS_IDENT}:${BuildConfig.XIRSYS_SECRET}".toByteArray()
        val authToken = "Basic ${Base64.encodeToString(data, Base64.NO_WRAP)}"
        return api.getIceServers(authToken)
    }
}