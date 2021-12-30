package com.webrtcdemo.webrtc_phone_app.use_case

import com.webrtcdemo.webrtc_phone_app.common.Resource
import com.webrtcdemo.webrtc_phone_app.data.remote.dto.toIceServerList
import com.webrtcdemo.webrtc_phone_app.data.repository.IceServerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.webrtc.PeerConnection
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class GetIceServersUseCase @Inject constructor(
    private val repository: IceServerRepository
) {
    operator fun invoke(): Flow<Resource<List<PeerConnection.IceServer>>> = flow {
        try {
            val iceServers = repository.getIceServers().iceServerDetailList.toIceServerList()
            if (iceServers == null) {
                emit(Resource.Error("The ice server list was null"))
                return@flow
            }
            emit(Resource.Success(iceServers))
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        } catch (e: IOException) {
            emit(Resource.Error("Couldn't reach server. Check your internet connection."))
        }
    }
}