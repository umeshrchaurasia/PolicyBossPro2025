package com.policyboss.policybosspro.view.others.contactUS

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.policyboss.policybosspro.core.response.contactUs.ContactUsEntity
import com.policyboss.policybosspro.databinding.LayoutContactusItemBinding

class ContactUsAdapter(
    private val context: Context,
    private val whatsNewEntities: List<ContactUsEntity>,
    private val onSupportNoItemClick: (ContactUsEntity) -> Unit ,
    private val onEmailItemClick: (ContactUsEntity) -> Unit,
    private val onWebsiteClick: (ContactUsEntity) -> Unit,
) : RecyclerView.Adapter<ContactUsAdapter.WhatsNewItem>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WhatsNewItem {
        val binding = LayoutContactusItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WhatsNewItem(binding)
    }

    override fun onBindViewHolder(holder: WhatsNewItem, position: Int) {
        val entity = whatsNewEntities[position]
        
        with(holder.binding) {
            tvTitle.text = entity.Header
            tvSupportNo.text = entity.PhoneNo
            tvEmail.text = entity.Email
            tvWebsite.text = entity.Website
            tvDisplayTitle.text = entity.DisplayTitle

            // Set click listeners for each view with entity and view context
            tvSupportNo.setOnClickListener { onSupportNoItemClick(entity) }
            tvEmail.setOnClickListener { onEmailItemClick(entity) }
            tvWebsite.setOnClickListener { onWebsiteClick(entity) }
        }
    }

    override fun getItemCount(): Int = whatsNewEntities.size

    inner class WhatsNewItem(val binding: LayoutContactusItemBinding) : RecyclerView.ViewHolder(binding.root)
}
