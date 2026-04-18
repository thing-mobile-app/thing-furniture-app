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
import com.example.thingapp.databinding.CartProductItemBinding
import com.example.thingapp.helper.getProductPrice

/**
 * Adapter class for managing and displaying a list of [CartProduct] items in the shopping cart.
 * * This adapter handles real-time updates via [AsyncListDiffer], item visibility based on
 * user selections (color/size), and provides callbacks for quantity adjustments and navigation.
 */
class CartProductAdapter : RecyclerView.Adapter<CartProductAdapter.CartProductsViewHolder>() {

    /**
     * ViewHolder for cart items.
     * Responsible for binding product data, calculating discounted prices, and managing attribute visibility.
     */
    inner class CartProductsViewHolder(val binding: CartProductItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * Binds the [CartProduct] data to the layout views.
         * * @param cartProduct The data object containing product details, selected attributes, and quantity.
         */
        fun bind(cartProduct: CartProduct) {
            binding.apply {
                // Load product image efficiently using Glide
                Glide.with(itemView)
                    .load(cartProduct.product.images.getOrNull(0))
                    .into(imageCartProduct)

                // Display basic product information
                tvProductCartName.text = cartProduct.product.name
                tvCartProductQuantity.text = cartProduct.quantity.toString()

                /**
                 * Price calculation considering the offer percentage.
                 * Formula used: $P_{final} = P_{original} \times (1 - offer)$
                 */
                val priceAfterPercentage = cartProduct.product.offerPercentage.getProductPrice(cartProduct.product.price)
                tvProductCartPrice.text = "$ ${String.format("%.2f", priceAfterPercentage)}"

                // Manage Color Indicator visibility
                if (cartProduct.selectedColor != null) {
                    imageCartProductColor.visibility = View.VISIBLE
                    imageCartProductColor.setImageDrawable(ColorDrawable(cartProduct.selectedColor))
                } else {
                    imageCartProductColor.visibility = View.GONE
                }

                /**
                 * Manage Size Indicator visibility.
                 * Note: Using 'selectedsize' as defined in the Data Class.
                 */
                if (cartProduct.selectedsize != null) {
                    tvCartProductSize.visibility = View.VISIBLE
                    imageCartProductSize.visibility = View.VISIBLE
                    tvCartProductSize.text = cartProduct.selectedsize
                } else {
                    tvCartProductSize.visibility = View.GONE
                    imageCartProductSize.visibility = View.GONE
                }
            }
        }
    }

    /**
     * DiffUtil callback implementation to optimize list updates by identifying changes.
     */
    private val diffCallback = object : DiffUtil.ItemCallback<CartProduct>() {
        override fun areItemsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem.product.id == newItem.product.id
        }

        override fun areContentsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem == newItem
        }
    }

    /**
     * Public access to the AsyncListDiffer for submitting new lists.
     */
    val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartProductsViewHolder {
        return CartProductsViewHolder(
            CartProductItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: CartProductsViewHolder, position: Int) {
        val cartProduct = differ.currentList[position]
        holder.bind(cartProduct)

        // Navigation to product details
        holder.itemView.setOnClickListener {
            onProductClick?.invoke(cartProduct)
        }

        // Increase quantity listener
        holder.binding.imagePlus.setOnClickListener {
            onPlusClick?.invoke(cartProduct)
        }

        // Decrease quantity listener
        holder.binding.imageMinus.setOnClickListener {
            onMinusClick?.invoke(cartProduct)
        }
    }

    override fun getItemCount(): Int = differ.currentList.size

    /** Callback triggered when the entire product item is clicked. */
    var onProductClick: ((CartProduct) -> Unit)? = null

    /** Callback triggered when the plus (+) button is clicked. */
    var onPlusClick: ((CartProduct) -> Unit)? = null

    /** Callback triggered when the minus (-) button is clicked. */
    var onMinusClick: ((CartProduct) -> Unit)? = null
}