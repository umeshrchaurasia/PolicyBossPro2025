package com.policyboss.policybosspro.view.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.policyboss.policybosspro.core.response.home.UserCallingEntity
import com.policyboss.policybosspro.databinding.LayoutCallingUserdetailItemBinding

class CallingDetailAdapter (

    private val lstCallingDetail: List<UserCallingEntity>,
    private val onMobileClick: (UserCallingEntity) -> Unit,
    private val onEmailClick: (UserCallingEntity) -> Unit
): RecyclerView.Adapter<CallingDetailAdapter.CallingDetailItem>(){

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CallingDetailAdapter.CallingDetailItem {

        val binding = LayoutCallingUserdetailItemBinding.inflate(
            LayoutInflater.from(parent.context),parent,false
        )
        return CallingDetailItem(binding)
    }

    override fun onBindViewHolder(holder: CallingDetailAdapter.CallingDetailItem, position: Int) {

        val entity = lstCallingDetail[position]

        holder.bind(userCallingEntity = entity)
    }


    inner class CallingDetailItem(val binding: LayoutCallingUserdetailItemBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(userCallingEntity: UserCallingEntity) {

            with(binding) {
                txtName.text = userCallingEntity.EmployeeName
                txtDesig.text = userCallingEntity.Designation
                txtMobile.text = userCallingEntity.MobileNo
                txtEmail.text = userCallingEntity.EmailId

                lyMobile.setOnClickListener {
                    onMobileClick(userCallingEntity)
                }

                lyEmail.setOnClickListener {
                    onEmailClick(userCallingEntity)
                }
            }
        }

    }
    override fun getItemCount(): Int = lstCallingDetail.size


}