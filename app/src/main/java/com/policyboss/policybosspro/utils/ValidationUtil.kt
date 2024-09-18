package com.policyboss.policybosspro.utils

import android.util.Patterns
import android.widget.EditText

object ValidationUtil {
    fun isValidEmailID(editText: EditText): Boolean {
        val emailEntered = editText.text.toString().trim()
        val emailPattern = Patterns.EMAIL_ADDRESS
        return !emailEntered.isEmpty() && emailPattern.matcher(emailEntered).matches()
    }
}