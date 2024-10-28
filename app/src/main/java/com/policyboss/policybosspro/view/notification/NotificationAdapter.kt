package com.policyboss.policybosspro.view.notification

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.policyboss.policybosspro.R
import com.policyboss.policybosspro.core.response.notification.NotificationEntity
import com.policyboss.policybosspro.core.response.salesMaterial.DocEntity
import com.policyboss.policybosspro.databinding.PushNotifyItemBinding


class NotificationAdapter(
    private val mContext: Context,
    private var notificationList: MutableList<NotificationEntity>,
    private val onItemClicked: ((NotificationEntity) -> Unit)? = null

) : RecyclerView.Adapter<NotificationAdapter.NotificationItem>() {

    inner class NotificationItem(private val binding: PushNotifyItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(notificationEntity: NotificationEntity) {
            binding.apply {
                txtTitle.text = notificationEntity.title
                txtMessage.text = notificationEntity.body
                txtDate.text = notificationEntity.date

                Glide.with(mContext)
                    .load(notificationEntity.img_url)
                    .placeholder(R.drawable.notification_ic)
                    .into(ivNotify)

                if (! notificationEntity.img_url.isNullOrEmpty()) {
                    rlBigImg.visibility = View.GONE
                    viewBigImg.visibility = View.GONE
                    imgArrow.visibility = View.INVISIBLE
                } else {
                    if (notificationEntity.isOpen) {
                        rlBigImg.visibility = View.GONE
                        viewBigImg.visibility = View.GONE
                        imgArrow.visibility = View.INVISIBLE
                    } else {
                        rlBigImg.visibility = View.GONE
                        viewBigImg.visibility = View.GONE
                        imgArrow.visibility = View.INVISIBLE

                        Glide.with(mContext)
                            .load(notificationEntity.img_url)
                            .into(imgBigNotify)
                    }
                }

//                lyParent.setOnClickListener { onItemClicked(notificationEntity) }
//                rlArrow.setOnClickListener { updateList(notificationEntity) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationItem {
        val binding = PushNotifyItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotificationItem(binding)
    }

    override fun onBindViewHolder(holder: NotificationItem, position: Int) {
        val notificationEntity = notificationList[position]
        holder.bind(notificationEntity)
    }

    override fun getItemCount(): Int {
        return notificationList.size
    }


    fun updateNotifyList(newList: List<NotificationEntity>) {
        notificationList.clear()
        notificationList.addAll(newList)
        notifyDataSetChanged() // Notify adapter that data has changed
    }


    private fun updateList(notificationEntity: NotificationEntity) {
        val pos = notificationList.indexOfFirst { it.message_id == notificationEntity.message_id }

        if (pos != -1) {
            // Toggle the open state
            notificationList[pos].isOpen = !notificationList[pos].isOpen

            // Notify the adapter of the change
            notifyItemChanged(pos, notificationEntity)
        }
    }

}