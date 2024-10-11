package com.policyboss.policybosspro.core.response.horizonResponse

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type





data class Horizon_sync_contact_agree_Response(
    //val Msg: List<sync_contact_agree>,
    val Msg: Any? = null,
    val Status: String
)

data class sync_contact_agree(
//    val Created_On: String,
//    val Modified_On: String,
 //   val Sync_Contact_Agreement_Id: Int,

    //val _id: String,
    val fba_id: Int,
    val is_call: String,
    val is_sms: String,
//    val online_agreement: String,
    val ss_id: Int
)

