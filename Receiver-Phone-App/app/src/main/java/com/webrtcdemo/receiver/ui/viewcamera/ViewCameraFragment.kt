package com.webrtcdemo.receiver.ui.viewcamera

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.webrtcdemo.receiver.databinding.ViewCameraFragmentBinding

class ViewCameraFragment : Fragment() {

    private lateinit var viewCameraViewModel: ViewCameraViewModel
    private var _binding: ViewCameraFragmentBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewCameraViewModel =
            ViewModelProvider(this).get(ViewCameraViewModel::class.java)

        _binding = ViewCameraFragmentBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        viewCameraViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}