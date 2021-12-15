package com.webrtcdemo.webrtc_phone_app.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.webrtcdemo.webrtc_phone_app.databinding.RegisterCameraFragmentBinding
import com.webrtcdemo.webrtc_phone_app.viewmodel.RegisterCameraViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterCameraFragment : Fragment() {

    private lateinit var registerCameraViewModel: RegisterCameraViewModel
    private var _binding: RegisterCameraFragmentBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        registerCameraViewModel =
            ViewModelProvider(this).get(RegisterCameraViewModel::class.java)

        _binding = RegisterCameraFragmentBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textDashboard
        registerCameraViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}