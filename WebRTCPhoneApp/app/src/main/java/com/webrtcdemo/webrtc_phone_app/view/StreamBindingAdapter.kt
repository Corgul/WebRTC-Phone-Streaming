package com.webrtcdemo.webrtc_phone_app.view

import android.view.View
import androidx.databinding.BindingAdapter
import com.webrtcdemo.webrtc_phone_app.webrtc.StreamEvent

@BindingAdapter("isLoadingVisible")
fun bindIsLoadingVisible(view: View, event: StreamEvent?) {
    view.visibility = if (event == StreamEvent.CONNECTING) {
        View.VISIBLE
    } else {
        View.GONE
    }
}

@BindingAdapter("isSurfaceVisible")
fun bindIsSurfaceVisible(view: View, event: StreamEvent?) {
    view.visibility = if (event == StreamEvent.CONNECTED) {
        View.VISIBLE
    } else {
        View.GONE
    }
}

/**
 * MutableSharedFlow emits null value initially when using data binding even though the Flow is non-null
 */
@BindingAdapter("isRoomEntryVisible")
fun bindIsRoomEntryVisible(view: View, event: StreamEvent?) {
    view.visibility = if (event == StreamEvent.NOT_STARTED || event == null) {
        View.VISIBLE
    } else {
        View.GONE
    }
}