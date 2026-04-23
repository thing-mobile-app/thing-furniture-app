package com.example.thingapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.thingapp.R
import com.example.thingapp.data.order.Order
import com.example.thingapp.data.order.OrderStatus
import com.example.thingapp.data.order.getOrderStatus
import com.example.thingapp.databinding.OrderItemBinding

/**
 * RecyclerView adapter for displaying a list of user orders.
 *
 * Each item shows a modern pill-shaped status badge with a progressive
 * color scheme: Yellow (Ordered) → Orange (Confirmed) → Lime (Shipped) → Green (Delivered).
 */
class AllOrdersAdapter : RecyclerView.Adapter<AllOrdersAdapter.OrdersViewHolder>() {

    /**
     * ViewHolder that binds [Order] data to the order item layout.
     */
    inner class OrdersViewHolder(private val binding: OrderItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * Binds the given [order] to the UI.
         * Sets the order ID, date, and a styled status badge (background, label, subtitle, text color)
         * based on the progressive order status palette.
         */
        fun bind(order: Order) {
            binding.apply {
                tvOrderId.text = itemView.context.getString(R.string.order_id_format, order.orderId.toString())
                tvOrderDate.text = order.date
                val ctx = itemView.context

                data class StatusAppearance(
                    val bgDrawable: Int,
                    val labelText: String,
                    val subtitleText: String,
                    val textColor: Int
                )

                val appearance = when (getOrderStatus(order.orderStatus)) {
                    is OrderStatus.Ordered -> StatusAppearance(
                        bgDrawable  = R.drawable.status_badge_ordered,
                        labelText   = ctx.getString(R.string.status_ordered),
                        subtitleText = ctx.getString(R.string.status_sub_ordered),
                        textColor   = ContextCompat.getColor(ctx, R.color.status_ordered_text)
                    )
                    is OrderStatus.Confirmed -> StatusAppearance(
                        bgDrawable  = R.drawable.status_badge_confirmed,
                        labelText   = ctx.getString(R.string.status_confirmed),
                        subtitleText = ctx.getString(R.string.status_sub_confirmed),
                        textColor   = ContextCompat.getColor(ctx, R.color.status_confirmed_text)
                    )
                    is OrderStatus.Shipped -> StatusAppearance(
                        bgDrawable  = R.drawable.status_badge_shipped,
                        labelText   = ctx.getString(R.string.status_shipped),
                        subtitleText = ctx.getString(R.string.status_sub_shipped),
                        textColor   = ContextCompat.getColor(ctx, R.color.status_shipped_text)
                    )
                    is OrderStatus.Delivered -> StatusAppearance(
                        bgDrawable  = R.drawable.status_badge_delivered,
                        labelText   = ctx.getString(R.string.status_delivered),
                        subtitleText = ctx.getString(R.string.status_sub_delivered),
                        textColor   = ContextCompat.getColor(ctx, R.color.status_delivered_text)
                    )
                    is OrderStatus.Canceled -> StatusAppearance(
                        bgDrawable  = R.drawable.status_badge_canceled,
                        labelText   = ctx.getString(R.string.status_canceled),
                        subtitleText = ctx.getString(R.string.status_sub_canceled),
                        textColor   = ContextCompat.getColor(ctx, R.color.status_canceled_text)
                    )
                    else -> StatusAppearance(
                        bgDrawable  = R.drawable.status_badge_ordered,
                        labelText   = order.orderStatus,
                        subtitleText = "",
                        textColor   = ContextCompat.getColor(ctx, R.color.status_ordered_text)
                    )
                }

                llStatusBadge.setBackgroundResource(appearance.bgDrawable)
                tvStatusLabel.text = appearance.labelText
                tvStatusLabel.setTextColor(appearance.textColor)
                tvStatusSubtitle.text = appearance.subtitleText
                tvStatusSubtitle.setTextColor(appearance.textColor)
            }
        }
    }

    private val diffUtil = object : DiffUtil.ItemCallback<Order>() {
        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem.orderId == newItem.orderId
        }

        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem == newItem
        }
    }

    /** Handles efficient list updates using DiffUtil. */
    val differ = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdersViewHolder {
        return OrdersViewHolder(
            OrderItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: OrdersViewHolder, position: Int) {
        val order = differ.currentList[position]
        holder.bind(order)

        /**
         * Triggers item click callback when the user taps the order item.
         * Passes the selected order object to the listener.
         */
        holder.itemView.setOnClickListener { onClick?.invoke(order) }
        holder.itemView.findViewById<android.widget.ImageView>(R.id.ivDeleteOrder)
            .setOnClickListener { onDeleteClick?.invoke(order) }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    /** Callback triggered when an order item is clicked. */
    var onClick: ((Order) -> Unit)? = null

    /** Callback triggered when the delete button on an order item is clicked. */
    var onDeleteClick: ((Order) -> Unit)? = null
}
