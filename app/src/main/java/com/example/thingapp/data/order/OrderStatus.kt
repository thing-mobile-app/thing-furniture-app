package com.example.thingapp.data.order

/**
 * Sealed class representing the five statuses of an order.
 * The flow is: Ordered → Confirmed → Shipped → Delivered.
 * Orders can also be Canceled.
 */
sealed class OrderStatus(val status: String) {
    object Ordered   : OrderStatus("Ordered")
    object Confirmed : OrderStatus("Confirmed")
    object Shipped   : OrderStatus("Shipped")
    object Delivered : OrderStatus("Delivered")
    object Canceled  : OrderStatus("Canceled")
}

/**
 * Converts a status string to its corresponding [OrderStatus] object.
 * Defaults to [OrderStatus.Ordered] if the string doesn't match any known status.
 */
fun getOrderStatus(status: String): OrderStatus = when (status) {
    "Confirmed" -> OrderStatus.Confirmed
    "Shipped"   -> OrderStatus.Shipped
    "Delivered" -> OrderStatus.Delivered
    "Canceled", "Cancelled" -> OrderStatus.Canceled
    else -> OrderStatus.Ordered
}