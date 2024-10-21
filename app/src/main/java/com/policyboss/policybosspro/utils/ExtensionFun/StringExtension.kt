package com.policyboss.demoandroidapp.Utility.ExtensionFun

import com.policyboss.policybosspro.utils.DateValidator


fun String.isValidDate(format: String = "dd-MM-yyyy") = DateValidator(format).isValid(this)