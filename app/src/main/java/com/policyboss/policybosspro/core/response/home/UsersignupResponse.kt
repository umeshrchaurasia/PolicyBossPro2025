package com.policyboss.policybosspro.core.response.home

data class UsersignupResponse(
    var masterData: List<UsersignupEntity>? = null
)

data class UsersignupEntity(
    var enableProSignupUrl: String? = "",
    var enableEliteSignupUrl: String? = "",
    var enableProPospUrl: String? = "",
    var enableProAddSubUserUrl: String? = ""
)