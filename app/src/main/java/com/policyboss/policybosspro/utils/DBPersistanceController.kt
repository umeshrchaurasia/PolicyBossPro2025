package com.policyboss.policybosspro.utils

import android.content.Context
import com.policyboss.policybosspro.R
import com.policyboss.policybosspro.core.model.homeDashboard.DashboardEntity
import com.policyboss.policybosspro.core.model.homeDashboard.DashboardMultiLangEntity
import com.policyboss.policybosspro.core.response.master.dynamicDashboard.DashBoardItemEntity
import com.policyboss.policybosspro.facade.PolicyBossPrefsManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DBPersistanceController @Inject constructor(
    private val mContext: Context,
    private val prefManager: PolicyBossPrefsManager
) {

    companion object {
        const val EXTERNAL_LPG = "External LPG"
        const val EXTERNAL_CNG = "External CNG"
        const val INSURER_LIST = "insurer_list"
        const val FOS_DETAIL = "fos_detail"
    }


    private lateinit var dashBoardItemEntities: List<DashBoardItemEntity>



    /*
    fun getInsurProductList(): List<DashboardEntity> {
        val dashboardEntities = mutableListOf<DashboardEntity>()

        // region Now Insurance Dashboard Dynamically added from server


        // Add predefined insurance products
        dashboardEntities.addAll(listOf(
            DashboardEntity("INSURANCE", 1, "PRIVATE CAR",
                "Best quotes for Private Car Insurance of your customers with instant policy.",
                R.drawable.private_car),
            DashboardEntity("INSURANCE", 10, "TWO WHEELER",
                "Best quotes for Two Wheeler Insurance of your customers with instant policy.",
                R.drawable.two_wheeler),
            DashboardEntity("INSURANCE", 12, "COMMERCIAL VEHICLE",
                "Best quotes for CV Insurance of your customers with instant policy.",
                R.drawable.commercial_vehicle),
            DashboardEntity("INSURANCE", 3, "HEALTH INSURANCE",
                "Get quotes, compare benefits and buy online from top Health Insurance companies.",
                R.drawable.health_insurance),
            DashboardEntity("INSURANCE", 18, "TERM INSURANCE",
                "Get quotes, compare benefits and buy online from top Life Insurance companies.",
                R.drawable.life_insurance),
            DashboardEntity("INSURANCE", 16, "REQUEST OFFLINE QUOTES",
                "Get offline quotes.", R.drawable.offlineportal)
        ))
        // endregion

        // Add dashboard items from preferences
        prefManager.getMenuDashBoard()?.masterData?.dashboard?.let { dashBoardItems ->
            dashboardEntities.addAll(
                dashBoardItems
                    .filter { it.dashboard_type == "1" && it.isActive == 1 }
                    .map { item ->
                        DashboardEntity("INSURANCE", item.sequence.toInt(), item.menuname,
                            item.description, -1).apply {
                            serverIcon = item.iconimage
                            link = item.link
                        }
                    }
            )
        }

        return dashboardEntities
    }


     */

}
