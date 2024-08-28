package com.policyboss.policybosspro.core.response.horizonResponse

data class Horizon_sync_contact_agree_Response(
    val Msg: List<sync_contact_agree>,
    val Status: String
)

data class sync_contact_agree(
    val Created_On: String,
    val Modified_On: String,
    val Sync_Contact_Agreement_Id: Int,
    val __v: Int,
    val _id: String,
    val fba_id: Int,
    val is_call: String,
    val is_sms: String,
    val online_agreement: String,
    val ss_id: Int
)