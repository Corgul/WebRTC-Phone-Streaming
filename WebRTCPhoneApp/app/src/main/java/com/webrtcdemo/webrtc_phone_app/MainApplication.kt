package com.webrtcdemo.webrtc_phone_app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
//Goce - do we need this only because of Hilt? I don't see any use of it
class MainApplication : Application()