package com.policyboss.policybosspro.view.qrScanner.dialog

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.policyboss.policybosspro.R
import com.policyboss.policybosspro.databinding.DialogInvalidQrBinding


class InvalidQrBottomSheet : BottomSheetDialogFragment() {

    private var _binding: DialogInvalidQrBinding? = null
    private val binding get() = _binding!!

    var onRetryClick: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = DialogInvalidQrBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        fun newInstance(): InvalidQrBottomSheet {
            return InvalidQrBottomSheet()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnRetry.setOnClickListener {

             // close dialog and allow scanner again
            onRetryClick?.invoke()
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}