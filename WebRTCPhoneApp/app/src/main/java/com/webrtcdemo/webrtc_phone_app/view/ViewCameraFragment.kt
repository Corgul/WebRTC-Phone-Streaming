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
    //Goce - not releavant for the  project but you have nested constraint views
    // the whole point of them is to have a flat UI, we can use groups to overcome the challenges
    // and use one constraint layout
    private lateinit var binding: ViewCameraFragmentBinding
    // Goce - I think we should explain what this is when we do the presentation since this is a webrtc type of view
    private lateinit var surface: SurfaceViewRenderer

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ViewCameraFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        surface = binding.videoView
        //Goce - this is important, to explain in the presentation
        // we register the fragment as a callback for the "video surface" callback events
        // so that when the surface is created we can "attach it" to the stream (I think)
        surface.holder.addCallback(this)
        return binding.root
    }

    // Goce - surfaceCreated,surfaceDestroyed  and surfaceChanged are important to be explained in the presentation
    override fun surfaceCreated(holder: SurfaceHolder) {
        //Goce - we need to explain this line
        surface.init(EglBaseWrapper.eglBase.eglBaseContext, null)
        viewModel.initVideoSink(surface)
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        viewModel.unbindVideoSink()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
    }
}