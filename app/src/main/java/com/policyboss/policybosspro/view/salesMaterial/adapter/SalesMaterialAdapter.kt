package com.policyboss.policybosspro.view.salesMaterial.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.policyboss.policybosspro.core.response.salesMaterial.SalesMateriaProdEntity
import com.policyboss.policybosspro.databinding.LayoutSalesProductItemBinding
import com.policyboss.policybosspro.facade.PolicyBossPrefsManager


//Mark : Sales Material Adapter
class SalesMaterialAdapter(

    private val context : Context,
    private var salesProductList: List<SalesMateriaProdEntity>,
    private val prefsManager : PolicyBossPrefsManager,
    private val onItemClick : (SalesMateriaProdEntity,Int) -> Unit
) : RecyclerView.Adapter<SalesMaterialAdapter.SalesMaterialItem> () {



    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SalesMaterialItem {
        val binding = LayoutSalesProductItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SalesMaterialItem(binding)
    }

    override fun onBindViewHolder(holder: SalesMaterialItem, position: Int) {

        val entity = salesProductList[position]

        holder.bind(entity = entity, pos = position)
    }

    inner class SalesMaterialItem(val binding : LayoutSalesProductItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(entity: SalesMateriaProdEntity, pos: Int) {

            with(binding) {

                // Set visibility and count
                if (entity.Count == entity.OldCount) {
                    txtCount.visibility = View.INVISIBLE
                } else {
                    txtCount.visibility = View.VISIBLE
                    txtCount.text = (entity.Count - entity.OldCount).toString()
                }

                // Set product name and image
                txtProductName.text = entity.Product_Name
                Glide.with(context).load(entity.Product_image).into(imgProduct)

                lyParent.setOnClickListener {

                    onItemClick(entity, position)
                }
            }
        }
    }

    override fun getItemCount(): Int = salesProductList.size


    // Function to update list and persist data
    fun updateList(salesProductEntity: SalesMateriaProdEntity, pos: Int) {
        salesProductList[pos].OldCount = salesProductEntity.Count
        notifyItemChanged(pos, salesProductEntity)
       // dbPersistanceController.updateCompanyList(salesProductList)
    }



}



