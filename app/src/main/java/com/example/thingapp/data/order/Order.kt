package com.example.thingapp.data.order

import com.example.thingapp.data.Address
import com.example.thingapp.data.CartProduct
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random.Default.nextLong

/**
 * Data class representing a completed purchase order.
 */
data class Order(
    val orderStatus: String = "",
    val totalPrice: Float = 0f,
    val products: List<CartProduct> = emptyList(),
    val address: Address = Address(),
    val date: String = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(Date()),
    val orderId: Long = nextLong(0, 100_000_000_000) + totalPrice.toLong()
)