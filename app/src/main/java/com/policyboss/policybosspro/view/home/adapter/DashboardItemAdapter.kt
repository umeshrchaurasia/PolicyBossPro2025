package com.policyboss.policybosspro.view.home.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.policyboss.policybosspro.R
import com.policyboss.policybosspro.analytics.WebEngageAnalytics
import com.policyboss.policybosspro.core.model.homeDashboard.DashboardMultiLangEntity
import com.policyboss.policybosspro.databinding.DashboardRvItemBinding
import com.policyboss.policybosspro.facade.PolicyBossPrefsManager
import com.policyboss.policybosspro.utils.DBPersistanceController
import com.policyboss.policybosspro.utils.NetworkUtils
import com.policyboss.policybosspro.utils.showSnackbar
import com.policyboss.policybosspro.view.home.HomeActivity
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject



class DashboardItemAdapter(
    private val context: Context,
    private val listInsur: List<DashboardMultiLangEntity>,
    private val onShareClick: (DashboardMultiLangEntity) -> Unit,
    private val onInfoClick: (DashboardMultiLangEntity) -> Unit,
    private val onDashBoardClick: (DashboardMultiLangEntity) -> Unit


) : RecyclerView.Adapter<DashboardItemAdapter.DashboardItemHolder>(){


//    @Inject
//    lateinit var prefsManager: PolicyBossPrefsManager
//    private var fbaId: Int = prefsManager.getFBAID().toIntOrNull() ?: 0
//    private var loanUrl: String = ""

    inner class DashboardItemHolder(val binding: DashboardRvItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardItemHolder {
        val binding = DashboardRvItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DashboardItemHolder(binding)
    }

    override fun onBindViewHolder(holder: DashboardItemHolder, position: Int) {
        val item = listInsur[position]

        with(holder.binding) {
            // Load icon
            if (item.iconResId == -1) {
                Glide.with(context).load(item.serverIcon).into(imgIcon)
            } else {
                imgIcon.setImageResource(item.iconResId)
            }

            txtProductName.text = item.menuName
            txtProductDesc.text = item.description

            //region Set share and info visibility
            imgShare.visibility = if (item.isSharable == "Y") View.VISIBLE else View.GONE
            imgInfo.visibility = if (item.info.isNotEmpty()) View.VISIBLE else View.GONE
            //endregion

            //region Set background and font colors
            lyParent.setBackgroundColor(item.productBackgroundColor?.takeIf { it.isNotEmpty() }?.let {
                Color.parseColor("#$it")
            } ?: ContextCompat.getColor(context, R.color.white))


            txtProductName.setTextColor(item.productNameFontColor?.takeIf { it.isNotEmpty() }?.let {
                Color.parseColor("#$it")
            } ?: ContextCompat.getColor(context, R.color.dashboard_title))

            txtProductDesc.setTextColor(item.productDetailsFontColor?.takeIf { it.isNotEmpty() }?.let {
                Color.parseColor("#$it")
            } ?: ContextCompat.getColor(context, R.color.header_text_color))
            //endregion

            // Show "New" icon if exclusive

            // region for Sharing Insurance Prod
            if (item.isExclusive == "Y") {
                imgNew.visibility = View.VISIBLE
                Glide.with(context).asGif().load(R.drawable.newicon).into(imgNew)
            } else {
                imgNew.visibility = View.GONE
            }
            //endregion


            imgShare.setOnClickListener{
                if (!NetworkUtils.isNetworkAvailable(context)) {

                    context.showSnackbar(imgShare,context.getString(R.string.noInternet))
                }
                else{
                    onShareClick(item)
                }

            }
            imgInfo.setOnClickListener{
                if (!NetworkUtils.isNetworkAvailable(context)) {

                    context.showSnackbar(imgShare,context.getString(R.string.noInternet))
                }
                else{
                    onInfoClick(item)
                }

            }

            lyParent.setOnClickListener{

                if (!NetworkUtils.isNetworkAvailable(context)) {

                    context.showSnackbar(imgShare,context.getString(R.string.noInternet))
                }
                else{
                    onDashBoardClick(item)
                }

            }


        }
    }

    override fun getItemCount(): Int = listInsur.size

}
