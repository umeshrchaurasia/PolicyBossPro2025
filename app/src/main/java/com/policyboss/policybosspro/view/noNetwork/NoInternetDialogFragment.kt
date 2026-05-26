package com.policyboss.policybosspro.view.noNetwork

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.policyboss.policybosspro.databinding.LayoutNoInternetBinding


class NoInternetDialogFragment : DialogFragment() {

    private var _binding: LayoutNoInternetBinding? = null

    private val binding get() = _binding!!

    override fun onCreateDialog(
        savedInstanceState: Bundle?
    ): Dialog {

        _binding =
            LayoutNoInternetBinding.inflate(layoutInflater)

        return Dialog(requireContext()).apply {

            setContentView(binding.root)

            setCancelable(false)

            window?.apply {

                setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )

                setBackgroundDrawable(
                    ColorDrawable(Color.TRANSPARENT)
                )
            }

            binding.btnRetry.setOnClickListener {

               // dismiss()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        const val TAG = "NoInternetDialog"
    }
}