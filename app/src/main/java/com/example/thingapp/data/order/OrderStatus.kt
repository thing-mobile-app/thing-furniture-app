package com.example.thingapp.data.order

/**
 * Represents the various lifecycle stages of a product order.
 *
 * Each state is associated with a string value for Firestore storage and UI display.
 */
sealed class OrderStatus(val status: String) {

    object Ordered: OrderStatus("Ordered")
    object Canceled: OrderStatus("Canceled")
    object Confirmed: OrderStatus("Confirmed")
    object Shipped: OrderStatus("Shipped")
    object Delivered: OrderStatus("Delivered")
    object Returned: OrderStatus("Returned")

}