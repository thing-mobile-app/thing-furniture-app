package com.example.thingapp.data

data class Product(
    val id: String = "",
    val name: String = "",
    val category: String = "",
    val price: Float = 0f,
    val offerPercentage: Float? = null,
    val description: String? = null,
    val colors: List<Int>? = null,
    val sizes: List<String>? = null,
    val images: List<String> = emptyList()
)