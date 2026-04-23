package com.example.thingapp.adapters

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
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
                Glide.with(itemView).load(billingProduct.product.images.getOrNull(0)).into(imageCartProduct)

                tvProductBillingName.text = billingProduct.product.name
                tvBillingProductQuantity.text = "Qty: ${billingProduct.quantity}"

                // Calculate the final price: apply the discount percentage if available,
                // otherwise fall back to the regular product price
                val priceAfterPercentage = billingProduct.product.offerPercentage?.let {
                    billingProduct.product.price - (billingProduct.product.price * it)
                } ?: billingProduct.product.price

                tvProductBillingPrice.text = "$ ${String.format("%.2f", priceAfterPercentage)}"

                // Bind selected color
                if (billingProduct.selectedColor != null) {
                    imageCartProductColor.visibility = View.VISIBLE
                    imageCartProductColor.setImageDrawable(ColorDrawable(billingProduct.selectedColor))
                } else {
                    imageCartProductColor.visibility = View.GONE
                }

                // Bind selected size
                if (billingProduct.selectedsize != null) {
                    imageCartProductSize.visibility = View.VISIBLE
                    tvCartProductSize.visibility = View.VISIBLE
                    
                    // Robust mapping to short letters for the billing circle
                    val displaySize = when(billingProduct.selectedsize.uppercase().trim()) {
                        "SMALL", "S" -> "S"
                        "MEDIUM", "M" -> "M"
                        "LARGE", "L" -> "L"
                        "EXTRA LARGE", "XL", "BIG", "EXTRA-LARGE" -> "XL"
                        else -> if (billingProduct.selectedsize.length > 2) billingProduct.selectedsize.substring(0, 1).uppercase() else billingProduct.selectedsize.uppercase()
                    }
                    tvCartProductSize.text = displaySize
                } else {
                    imageCartProductSize.visibility = View.GONE
                    tvCartProductSize.visibility = View.GONE
                }
            }
        }
    }

    private val diffUtil = object : DiffUtil.ItemCallback<CartProduct>() {
        override fun areItemsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem.product.id == newItem.product.id && oldItem.selectedColor == newItem.selectedColor && oldItem.selectedsize == newItem.selectedsize
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