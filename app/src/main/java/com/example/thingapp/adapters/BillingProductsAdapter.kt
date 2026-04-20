package com.example.thingapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.thingapp.data.CartProduct
import com.example.thingapp.databinding.BillingProductsRvItemBinding

/**
 * Adapter for displaying products in the billing/checkout screen.
 * It provides a simplified, read-only view of the cart items,
 * including their quantity and calculated final price.
 */
class BillingProductsAdapter : RecyclerView.Adapter<BillingProductsAdapter.BillingProductsViewHolder>() {

    inner class BillingProductsViewHolder(val binding: BillingProductsRvItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(billingProduct: CartProduct) {
            binding.apply {
                // Load the primary product image using Glide
                Glide.with(itemView).load(billingProduct.product.images[0]).into(imageCartProduct)

                tvProductBillingName.text = billingProduct.product.name
                tvBillingProductQuantity.text = "Qty: ${billingProduct.quantity}"

                // Calculate the final price: apply the discount percentage if available,
                // otherwise fall back to the regular product price
                val priceAfterPercentage = billingProduct.product.offerPercentage?.let {
                    billingProduct.product.price - (billingProduct.product.price * it)
                } ?: billingProduct.product.price

                tvProductBillingPrice.text = "$ ${String.format("%.2f", priceAfterPercentage)}"
            }
        }
    }

    private val diffUtil = object : DiffUtil.ItemCallback<CartProduct>() {
        override fun areItemsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem.product.id == newItem.product.id
        }

        override fun areContentsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillingProductsViewHolder {
        return BillingProductsViewHolder(
            BillingProductsRvItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: BillingProductsViewHolder, position: Int) {
        val billingProduct = differ.currentList[position]
        holder.bind(billingProduct)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}