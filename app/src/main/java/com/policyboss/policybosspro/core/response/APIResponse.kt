package com.policyboss.policybosspro.core.response

open  class APIResponse(
    val Message: String  = "",
    val Status: String = "",

    val StatusNo: Int = -1,

    // below req Old common resp
   // val Status : String = "",
   // val StatusNo : Int = -1
)