package com.webrtcdemo.webrtc_phone_app.view

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.webrtcdemo.webrtc_phone_app.R
import com.webrtcdemo.webrtc_phone_app.databinding.RegisterCameraFragmentBinding
import com.webrtcdemo.webrtc_phone_app.viewmodel.RegisterCameraViewModel
import com.webrtcdemo.webrtc_phone_app.webrtc.EglBaseWrapper
import dagger.hilt.android.AndroidEntryPoint
import org.webrtc.SurfaceViewRenderer

@AndroidEntryPoint
class RegisterCameraFragment : Fragment(), SurfaceHolder.Callback {

    private val viewModel: RegisterCameraViewModel by viewModels()
    private lateinit var binding: RegisterCameraFragmentBinding
    // SurfaceViewRenderer is like a normal SurfaceView but also implements VideoSink to make it easy to feed this surface into the video track
    // to display incoming frames
    private lateinit var surface: SurfaceViewRenderer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestCameraPermission()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = RegisterCameraFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        surface = binding.videoView
        // We register the fragment as a callback to know when the Surface has been created. That way we can attach it to the stream
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

    private fun requestCameraPermission() {
        val permissionName = Manifest.permission.CAMERA
        if (ContextCompat.checkSelfPermission(requireContext(), permissionName) == PackageManager.PERMISSION_GRANTED) {
            return
        }
        val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (!isGranted) {
                // If the user denies giving the permission, kick them back to the ViewCameraFragment
                showToast(getString(R.string.permission_denied_toast))
                findNavController().popBackStack()
            }
        }
        requestPermissionLauncher.launch(permissionName)
    }

    private fun showToast(text: String) {
        val toast = Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT)
        toast.show()
    }
}