package com.policyboss.policybosspro.core.requestbuilder.qrScanner

data class QRCodeRequest(

    val status: String,

    val ss_id: String,

    val token_id : String,

    val update_by: String,

    val secret_key: String,

    val client_key: String
)

data class QRCodePRERequest(

    val status: String,



    val token_id : String,

    val update_by: String,

    val secret_key: String,

    val client_key: String
)