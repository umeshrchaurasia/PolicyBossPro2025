package com.policyboss.policybosspro.core.response.master.dynamicDashboard

data class MenuMasterResponse(
    val MasterData: MenuMasterEntity,
    val Message: String?,
    val Status: String?,
    val StatusNo: Int
)

data class MenuMasterEntity(
    val Dashboard: List<DashBoardItemEntity>,
    val Menu: List<MenuItemEntity>
)