package com.example.thingapp.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Represents a product available in the e-commerce store.
 *
 * This data class holds all the details of a product, including its pricing,
 * available variations (colors, sizes), and display images.
 * All parameters have default values to automatically generate a no-argument
 * constructor, which is required for Firebase Firestore serialization.
 */
@Parcelize
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
) : Parcelable