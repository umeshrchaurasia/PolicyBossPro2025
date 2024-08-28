package com.policyboss.policybosspro.core.response.login

data class AuthLoginResponse(
    val Msg: AuthLoginMsg?,
    val SS_ID: String?,
    val Status: String
)


data class AuthLoginMsg(

    val Message: String,
    val ExceptionMessage: String? = null
//    val EmailID: String,
//    val EmpCode: Any,
//    val FBAId: Int,
//    val FBAStatus: String,
//    val FSM: String,
//    val Fullname: String,
//    val IsDemo: Any,
//    val IsFirstLogin: Int,
//    val IsMagiSale: String,
//    val Is_Employee: String,
//    val LIveURL: Int,
//    val LastloginDate: Any,
//    val LeadId: String,
//
//    val MobiNumb1: String,
//    val POSPStatus: String,
//    val Result: String,
//    val RewardPoint: Int,
//    val RoleId: Int,
//    val Status: Int,
//    val Sub_Fba_Id: Int,
//    val SuppAgentId: String,
//    val UserName: String,
//    val UserType: String,
//    val Validfrom: Any,
//    val agent_source: String,
//    val client_id: Int,
//    val strPassword: String
)