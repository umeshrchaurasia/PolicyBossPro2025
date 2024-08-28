package com.policyboss.policybosspro.core.requestbuilder.syncContact

data class SaveCheckboxRequestEntity(
    val fba_id: Int,
    val is_sms: String,
    val is_call: String,
    val online_agreement: String,
    val ss_id: Int,
    val app_version : String,
    val device_code : String
)