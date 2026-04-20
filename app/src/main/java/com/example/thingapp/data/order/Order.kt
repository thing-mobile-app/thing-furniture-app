package com.example.thingapp.data.order

import com.example.thingapp.data.Address
import com.example.thingapp.data.CartProduct

/**
 * Data class representing a completed purchase order.
 *
 * Includes the current status, total cost, list of purchased products,
 * and the designated shipping address.
 */
data class Order(
    val orderStatus: String,
    val totalPrice: Float,
    val products: List<CartProduct>,
    val address: Address
)