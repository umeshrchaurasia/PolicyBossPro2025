package com.policyboss.policybosspro.core.model.homeDashboard

data class DashboardEntity(
    var type: String = "",
    var productId: Int = 0,
    var productName: String = "",
    var productDetails: String = "",
    var icon: Int = -1,
    var link: String? = null,
    var serverIcon: String? = null,
    var id: String? = null,
    var kName: String? = null,
    var eTitle: String? = null,
    var hTitle: String? = null,
    var mTitle: String? = null,
    var gTitle: String? = null,
    var eDesc: String? = null,
    var hDesc: String? = null,
    var mDesc: String? = null,
    var gDesc: String? = null
)
