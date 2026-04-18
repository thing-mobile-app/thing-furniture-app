package com.example.thingapp.adapters

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.thingapp.data.CartProduct
import com.example.thingapp.data.Product
import com.example.thingapp.databinding.CartProductItemBinding
import com.example.thingapp.helper.getProductPrice

/**
 * Adapter for managing and displaying a list of [CartProduct] items in a [RecyclerView].
 *
 * It utilizes [AsyncListDiffer] for efficient list updates and provides multiple
 * click listeners to handle user interactions such as quantity changes and item navigation.
 */
class CartProductAdapter : RecyclerView.Adapter<CartProductAdapter.CartProductsViewHolder>() {

    /**
     * ViewHolder class that binds cart product data to the UI components.
     *
     * Handles image loading with Glide, color/size overlays, and price calculations
     * including discount offers via [getProductPrice].
     */
    inner class CartProductsViewHolder( val binding: CartProductItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * Binds the specific [cartProduct] data to the layout views.
         *
         * @param cartProduct The cart item containing product details and user selections.
         */
        fun bind(cartProduct: CartProduct) {
            binding.apply {
                Glide.with(itemView).load(cartProduct.product.images[0]).into(imageCartProduct)
                tvProductCartName.text = cartProduct.product.name
                tvCartProductQuantity.text = cartProduct.quantity.toString()

                val priceAfterPercentage = cartProduct.product.offerPercentage.getProductPrice(cartProduct.product.price)
                tvProductCartPrice.text = "$ ${String.format("%.2f", priceAfterPercentage)}"

                imageCartProductColor.setImageDrawable(ColorDrawable(cartProduct.selectedColor?: Color.TRANSPARENT))
                tvCartProductSize.text = cartProduct.selectedsize?: "".also { imageCartProductSize.setImageDrawable(ColorDrawable(Color.TRANSPARENT)) }
            }
        }
    }

    /**
     * Callback for calculating the diff between two non-null items in a list.
     * Used by [differ] to optimize UI updates.
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
     * Helper to compute the difference between two lists on a background thread.
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

        holder.itemView.setOnClickListener {
            onProductClick?.invoke(cartProduct)
        }
        holder.binding.imagePlus.setOnClickListener {
            onPlusClick?.invoke(cartProduct)
        }
        holder.binding.imageMinus.setOnClickListener {
            onMinusClick?.invoke(cartProduct)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    /**
     * Called when the user clicks on the entire item view.
     */
    var onProductClick: ((CartProduct) -> Unit)? = null
    /**
     * Called when the user clicks the plus (+) button to increase quantity.
     */
    var onPlusClick: ((CartProduct) -> Unit)? = null
    /**
     * Called when the user clicks the minus (-) button to decrease quantity.
     */
    var onMinusClick: ((CartProduct) -> Unit)? = null

}