package com.webrtcdemo.webrtc_phone_app.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.webrtcdemo.webrtc_phone_app.databinding.RegisterCameraFragmentBinding
import com.webrtcdemo.webrtc_phone_app.viewmodel.RegisterCameraViewModel
import com.webrtcdemo.webrtc_phone_app.webrtc.EglBaseWrapper
import dagger.hilt.android.AndroidEntryPoint
import org.webrtc.EglBase
import org.webrtc.SurfaceViewRenderer

@AndroidEntryPoint
class RegisterCameraFragment : Fragment(), SurfaceHolder.Callback {

    private val viewModel: RegisterCameraViewModel by viewModels()
    // Goce is this binding/layout any different than the other one?
    // Goce is anything different here? Maybe (not necesarry for the project) we can pull some of this code up in a base class
    private lateinit var binding: RegisterCameraFragmentBinding
    private lateinit var surface: SurfaceViewRenderer

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = RegisterCameraFragmentBinding.inflate(inflater, container, false)
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