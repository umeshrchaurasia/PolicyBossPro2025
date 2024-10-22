package com.policyboss.policybosspro.core.response.home

data class UserCallingResponse(
    val MasterData: List<UserCallingEntity?>,
    val Message: String,
    val Status: String,
    val StatusNo: Int
)

data class UserCallingEntity(
    val Designation: String,
    val EmailId: String,
    val EmployeeName: String,
    val MobileNo: String
)