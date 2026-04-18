package com.example.thingapp.helper

/**
 * Calculates the final price of a product after applying a discount percentage.
 *
 * This extension function operates on a nullable [Float] representing the discount
 * rate (e.g., 0.1f for a 10% discount). If no discount is provided, it returns
 * the original price.
 *
 * @receiver The discount percentage as a decimal (e.g., 0.2f for 20% off).
 * @param price The original price of the product before the discount.
 * @return The calculated price after applying the offer, or the original [price] if the discount is null.
 */
fun Float?.getProductPrice(price: Float): Float{
    // this --> Percentage
    if(this == null )
        return price
    val remainingPricePercentage = 1f - this
    val priceAfterOffer = remainingPricePercentage * price

    return priceAfterOffer
}