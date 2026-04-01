package com.example.thingapp.adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.thingapp.data.Product
import com.example.thingapp.databinding.BestDealsRvItemBinding

class BestDealsAdapter : RecyclerView.Adapter<BestDealsAdapter.BestDealsViewHolder>() {

    inner class BestDealsViewHolder(private val binding: BestDealsRvItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.apply {
                // Load product image with null-safety
                Glide.with(itemView).load(product.images.getOrNull(0)).into(imgBestDeal)

                tvDealProductName.text = product.name
                tvOldPrice.text = "$ ${product.price}"

                // Handle discount logic
                product.offerPercentage?.let { offer ->
                    // Calculate and display discounted price
                    val remainingPricePercentage = 1f - offer
                    val priceAfterOffer = remainingPricePercentage * product.price

                    tvNewPrice.text = "$ ${String.format("%.2f", priceAfterOffer)}"
                    tvNewPrice.visibility = View.VISIBLE

                    // Apply strikethrough effect to old price
                    tvOldPrice.paintFlags = tvOldPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                } ?: run {
                    // Hide discount field and remove strikethrough if no offer exists
                    tvNewPrice.visibility = View.INVISIBLE
                    tvOldPrice.paintFlags = tvOldPrice.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                }
            }
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean =
            oldItem == newItem
    }

    val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BestDealsViewHolder {
        return BestDealsViewHolder(
            BestDealsRvItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: BestDealsViewHolder, position: Int) {
        val product = differ.currentList[position]
        holder.bind(product)
    }

    override fun getItemCount(): Int = differ.currentList.size
}