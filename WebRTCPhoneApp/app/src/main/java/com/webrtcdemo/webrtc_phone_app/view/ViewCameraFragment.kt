package com.webrtcdemo.webrtc_phone_app.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.webrtcdemo.webrtc_phone_app.databinding.ViewCameraFragmentBinding
import com.webrtcdemo.webrtc_phone_app.viewmodel.ViewCameraViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ViewCameraFragment : Fragment() {

    private val viewModel: ViewCameraViewModel by viewModels()
    private lateinit var binding: ViewCameraFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ViewCameraFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        return binding.root
    }
}