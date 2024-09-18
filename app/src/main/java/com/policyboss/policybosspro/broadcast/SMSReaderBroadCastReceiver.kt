package com.policyboss.policybosspro.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import java.util.regex.Pattern

class SMSReaderBroadCastReceiver : BroadcastReceiver() {

    interface OTPReceiveListener {
        fun onOTPReceived(otp: String?)
        fun onOTPReceiveError(error: String)
    }

    private var otpListener: OTPReceiveListener? = null

    fun setOTPListener(otpListener: OTPReceiveListener?) {
        this.otpListener = otpListener
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {
            val extras = intent.extras
            val smsRetrieverStatus = getSmsRetrieverStatus(extras)

            when (smsRetrieverStatus?.statusCode) {
                CommonStatusCodes.SUCCESS -> {
                    val messageContent = extras?.getString(SmsRetriever.EXTRA_SMS_MESSAGE)
                    extractOTPFromMessage(messageContent)
                }
                CommonStatusCodes.TIMEOUT -> {
                    otpListener?.onOTPReceiveError("OTP retrieval timed out")
                }
                else -> {
                    otpListener?.onOTPReceiveError("OTP retrieval failed")
                }
            }
        }
    }

    private fun getSmsRetrieverStatus(extras: android.os.Bundle?): Status? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            extras?.getParcelable(SmsRetriever.EXTRA_STATUS, Status::class.java)
        } else {
            @Suppress("DEPRECATION")
            extras?.getParcelable(SmsRetriever.EXTRA_STATUS) as? Status
        }
    }

    private fun extractOTPFromMessage(message: String?) {
        message?.let {
            val otpPattern = Pattern.compile("\\d+")
            val matcher = otpPattern.matcher(it)
            if (matcher.find()) {
                val otp = matcher.group()
                otpListener?.onOTPReceived(otp)
            } else {
                otpListener?.onOTPReceiveError("OTP not found in the message")
            }
        } ?: otpListener?.onOTPReceiveError("Received message is null")
    }
}