package com.policyboss.policybosspro.core.response.contactUs

data class ContactUsResponse(
    val MasterData: List<ContactUsEntity>,
    val Message: String,
    val Status: String,
    val StatusNo: Int
)

data class ContactUsEntity(
    val DisplayTitle: String,
    val Email: String,
    val Header: String,
    val Id: Int,
    val PhoneNo: String,
    val Website: String
)