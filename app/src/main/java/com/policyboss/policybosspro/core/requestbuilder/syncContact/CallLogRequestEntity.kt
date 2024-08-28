package com.policyboss.policybosspro.core.requestbuilder.syncContact

import com.policyboss.policybosspro.core.model.sysncContact.CallLogEntity

data class CallLogRequestEntity(
    val call_history: List<CallLogEntity>,
    val fba_id: Int,
    val sub_fba_id: Int,
    var device_id : String,
    var app_version : String,
    val ss_id: Int
)
