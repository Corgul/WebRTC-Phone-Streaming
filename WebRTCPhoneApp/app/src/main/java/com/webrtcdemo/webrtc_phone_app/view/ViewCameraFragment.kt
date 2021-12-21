package com.webrtcdemo.webrtc_phone_app.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.webrtcdemo.webrtc_phone_app.databinding.ViewCameraFragmentBinding
import com.webrtcdemo.webrtc_phone_app.viewmodel.ViewCameraViewModel
import com.webrtcdemo.webrtc_phone_app.webrtc.EglBaseWrapper
import dagger.hilt.android.AndroidEntryPoint
import org.webrtc.SurfaceViewRenderer
import org.webrtc.VideoSink
import javax.inject.Inject

@AndroidEntryPoint
class ViewCameraFragment : Fragment(), SurfaceHolder.Callback {

    private val viewModel: ViewCameraViewModel by viewModels()
    private lateinit var binding: ViewCameraFragmentBinding
    private lateinit var surface: SurfaceViewRenderer

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ViewCameraFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        surface = binding.videoView
        surface.holder.addCallback(this)
        return binding.root
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        surface.init(EglBaseWrapper.eglBase.eglBaseContext, null)
        viewModel.initVideoSink(surface)
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        viewModel.unbindVideoSink()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
    }
}