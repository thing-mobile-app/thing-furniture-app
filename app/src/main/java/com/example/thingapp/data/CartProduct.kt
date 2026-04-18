package com.example.thingapp.data

/**
 * Represents a product added to the shopping cart.
 *
 * This data class wraps the base [Product] with specific user selections such as
 * quantity, color, and size.
 *
 * @property product The core [Product] information (name, price, etc.).
 * @property quantity The number of items the user wants to purchase.
 * @property selectedColor The ARGB color code selected by the user, if applicable.
 * @property selectedsize The size (e.g., "M", "L", "XL") selected by the user, if applicable.
 */
data class CartProduct(
    val product: Product,
    val quantity: Int,
    val selectedColor:Int? = null,
    val selectedsize: String? = null
){
    /**
     * No-argument constructor required for Firebase Firestore serialization.
     * Initializes the object with default values.
     */
    constructor(): this(Product(),1,null,null)
}
