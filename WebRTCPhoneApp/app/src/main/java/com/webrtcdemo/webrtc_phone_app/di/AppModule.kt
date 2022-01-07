package com.webrtcdemo.webrtc_phone_app.di

import com.webrtcdemo.webrtc_phone_app.common.Constants
import com.webrtcdemo.webrtc_phone_app.data.remote.IceServerApi
import com.webrtcdemo.webrtc_phone_app.data.repository.IceServerRepository
import com.webrtcdemo.webrtc_phone_app.data.repository.IceServerRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideIceServerApi(): IceServerApi {
        return Retrofit.Builder()
            .baseUrl(Constants.XIRSYS_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(IceServerApi::class.java)
    }

    @Provides
    @Singleton
    fun provideIceServerRepository(api: IceServerApi): IceServerRepository {
        return IceServerRepositoryImpl(api)
    }
}