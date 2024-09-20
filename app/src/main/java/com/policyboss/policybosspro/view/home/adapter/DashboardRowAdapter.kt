package com.policyboss.policybosspro.view.home.adapter

import com.policyboss.policybosspro.core.model.homeDashboard.DashboardMultiLangEntity

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.policyboss.policybosspro.R
import com.policyboss.policybosspro.databinding.LayoutDashboardDisclosureBinding
import com.policyboss.policybosspro.databinding.LayoutDashboardRecyclerBinding
import com.policyboss.policybosspro.facade.PolicyBossPrefsManager
import com.policyboss.policybosspro.utils.DBPersistanceController
import com.policyboss.policybosspro.webview.CommonWebViewActivity
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject



class DashboardRowAdapter(
     //private val mContext : Context,
     @ActivityContext private val mContext :Context,
    insurancePosition: Int,
    disclosurePosition: Int,
     private val listIns : List<DashboardMultiLangEntity>,
     private val prefsManager: PolicyBossPrefsManager,
     private val onShareClick: (DashboardMultiLangEntity) -> Unit,
     private val onInfoClick: (DashboardMultiLangEntity) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val ROW_INSURANCE = insurancePosition
    private val ROW_DISCLOSURE = disclosurePosition
    private val TOTAL_ROW = 2



//    @Inject
//    lateinit var mReal: DBPersistanceController



    inner class InsuranceHolder(private val binding: LayoutDashboardRecyclerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindInsuranceHolder() {
            //val listIns = getInsurProductLangList()
            binding.txtTypeName.text =
                "LANDMARK INSURANCE BROKERS PVT.LTD.\n(IRDAI CoR #216)"


            binding.ivLogo.setImageResource(R.drawable.logo_policyboss1)
            binding.rvDashboard.layoutManager = LinearLayoutManager(mContext)

            binding.rvDashboard.adapter = DashboardItemAdapter(
                                        context = mContext,
                                        listInsur = listIns,
                                        onShareClick = onShareClick,
                                        onInfoClick = onInfoClick

            )
        }
    }

    inner class DisclosureHolder(private val binding: LayoutDashboardDisclosureBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindDisclosureHolder() {
            binding.lyDisclosure.setOnClickListener {
                mContext.startActivity(Intent(mContext, CommonWebViewActivity::class.java).apply {
                    putExtra("URL", "file:///android_asset/Disclosure.html")
                    putExtra("NAME", "DISCLOSURE")
                    putExtra("TITLE", "DISCLOSURE")
                })
            }
        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ROW_INSURANCE -> {
                val binding = LayoutDashboardRecyclerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                InsuranceHolder(binding)
            }
            ROW_DISCLOSURE -> {
                val binding = LayoutDashboardDisclosureBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                DisclosureHolder(binding)
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (holder) {
            is InsuranceHolder -> holder.bindInsuranceHolder()
            is DisclosureHolder -> holder.bindDisclosureHolder()

        }
    }

    override fun getItemCount(): Int = TOTAL_ROW

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> ROW_INSURANCE
            1 -> ROW_DISCLOSURE
            else -> throw IllegalArgumentException("Invalid position")
        }
    }


    // Helper methods remain the same.
}
