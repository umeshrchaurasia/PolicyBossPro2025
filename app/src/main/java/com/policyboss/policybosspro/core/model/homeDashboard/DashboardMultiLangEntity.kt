package com.policyboss.policybosspro.core.model.homeDashboard



data class DashboardMultiLangEntity(
    var type: String = "",
    var productId: Int = 0,
    var productName: String = "",
    var productDetails: String = "",
    var icon: Int = 0,
    var productNameKey: String = "",
    var productDetailsKey: String = "",
    var productNameFontColor: String = "",
    var productDetailsFontColor: String = "",
    var productBackgroundColor: String = "",
    var isExclusive: String = "",
    var isNewprdClickable: String = "",
    var isSharable: String = "",
    var popupmsg: String = "",
    var title: String = "",
    var info: String = "",
    var isChecked: Boolean = false,
    var link: String = "",
    var serverIcon: String = ""
)
