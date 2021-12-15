package com.webrtcdemo.receiver.ui.registercamera

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RegisterCameraViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is the register camera fragment"
    }
    val text: LiveData<String> = _text
}