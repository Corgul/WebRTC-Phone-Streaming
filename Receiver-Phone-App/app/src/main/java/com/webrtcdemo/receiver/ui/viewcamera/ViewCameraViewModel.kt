package com.webrtcdemo.receiver.ui.viewcamera

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ViewCameraViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is the view camera fragment"
    }
    val text: LiveData<String> = _text
}