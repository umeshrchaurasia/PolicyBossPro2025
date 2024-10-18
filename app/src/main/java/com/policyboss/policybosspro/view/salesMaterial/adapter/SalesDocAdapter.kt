package com.policyboss.policybosspro.view.salesMaterial.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Transition
import coil.load
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.policyboss.policybosspro.R
import com.policyboss.policybosspro.core.response.salesMaterial.DocEntity
import com.policyboss.policybosspro.core.response.salesMaterial.SalesMateriaProdEntity
import com.policyboss.policybosspro.databinding.LayoutDocItemBinding

class SalesDocAdapter(
    private val context: Context,
    private var docList: MutableList<DocEntity>,
    private val onItemClick : (DocEntity) -> Unit
) : RecyclerView.Adapter<SalesDocAdapter.SalesDocItem>() {

    // ViewHolder class with ViewBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SalesDocItem {
        // Inflate using ViewBinding
        val binding = LayoutDocItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SalesDocItem(binding)
    }

    override fun onBindViewHolder(holder: SalesDocItem, position: Int) {
        val docEntity = docList[position]


        holder.bind(docEntity = docEntity)

    }

    inner class SalesDocItem(val binding: LayoutDocItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(docEntity: DocEntity) {

            with(binding){

                ivProduct.load(docEntity.image_path) {
                    size(300, 300)  // Set the dimensions of the image
                    placeholder(R.drawable.finmart_placeholder) // Placeholder while loading
                    error(R.drawable.finmart_placeholder)       // Error image in case of failure

                    target(
                        onSuccess = { drawable ->
                            // On successful image load
                            val bitmap = (drawable as BitmapDrawable).bitmap
                           ivProduct.setImageBitmap(bitmap)
                           lyParent.setOnClickListener {

                               //Lembda call back here
                               onItemClick(docEntity)
                           }
                        },
                        onError = {
                            // On failure to load the image
                           ivProduct.setImageResource(R.drawable.finmart_placeholder)
                           lyParent.setOnClickListener(null)
                        }
                    )
                }
            }


        }
    }


    override fun getItemCount(): Int = docList.size


    fun updateDocList(newList: List<DocEntity>) {
        docList.clear()
        docList.addAll(newList)
        notifyDataSetChanged() // Notify adapter that data has changed
    }
}
