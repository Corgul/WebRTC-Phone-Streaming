package com.webrtcdemo.webrtc_phone_app.view

import android.view.View
import androidx.databinding.BindingAdapter
import com.webrtcdemo.webrtc_phone_app.webrtc.StreamEvent

@BindingAdapter("isLoadingVisible")
fun bindIsLoadingVisible(view: View, event: StreamEvent) {
    if (event == StreamEvent.CONNECTING) {
        View.VISIBLE
    } else {
        View.GONE
    }
}

@BindingAdapter("isSurfaceVisible")
fun bindIsSurfaceVisible(view: View, event: StreamEvent) {
    if (event == StreamEvent.CONNECTED) {
        View.VISIBLE
    } else {
        View.GONE
    }
}

@BindingAdapter("isRoomEntryVisible")
fun bindIsRoomEntryVisible(view: View, event: StreamEvent) {
    if (event == StreamEvent.NOT_STARTED) {
        View.VISIBLE
    } else {
        View.GONE
    }
}