package com.policyboss.policybosspro.view.qrScanner.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.policyboss.policybosspro.R

import com.policyboss.policybosspro.databinding.DialogQrConfirmBinding
import com.policyboss.policybosspro.utils.showToast


class LoginConfirmBottomSheet : BottomSheetDialogFragment() {

    private var _binding: DialogQrConfirmBinding? = null
    private val binding get() = _binding!!

    var onConfirmClick: (() -> Unit)? = null
    var onCancelClick: (() -> Unit)? = null

    private var location: String? = null
    private var ip: String? = null
    private var device: String? = null

    companion object {

        private const val KEY_LOCATION = "key_location"
        private const val KEY_IP = "key_ip"
        private const val KEY_DEVICE = "key_device"

        fun newInstance(
            location: String,
            ip: String,
            device: String
        ): LoginConfirmBottomSheet {

            val fragment = LoginConfirmBottomSheet()

            val bundle = Bundle().apply {
                putString(KEY_LOCATION, location)
                putString(KEY_IP, ip)
                putString(KEY_DEVICE, device)
            }

            fragment.arguments = bundle

            return fragment
        }
    }


    override fun getTheme(): Int = R.style.AppBottomSheetTheme

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {

            location = it.getString(KEY_LOCATION)
            ip = it.getString(KEY_IP)
            device = it.getString(KEY_DEVICE)

        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
       // return inflater.inflate(R.layout.dialog_qr_confirm, container, false)

        _binding = DialogQrConfirmBinding.inflate(inflater, container, false)
        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvLocation.text = "$location"
        binding.tvIp.text = "$ip"
        binding.tvDevice.text = " $device"

        binding.btnCancel.setOnClickListener {
            onCancelClick?.invoke()
            dismiss()
        }

        binding.btnConfirm.setOnClickListener {

           // requireContext().showToast("Login Confirmed")
            showLoading()
            onConfirmClick?.invoke()

        }
    }


    private fun showLoading() {

        binding.progressBar.visibility = View.VISIBLE

        binding.btnConfirm.isEnabled = false
        binding.btnCancel.isEnabled = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}