package com.example.thingapp.firebase

import com.example.thingapp.data.CartProduct
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * A utility class providing common Firebase Firestore operations for the application.
 * This class handles database interactions related to the user's shopping cart,
 * including adding new products and managing item quantities.
 *
 * @property firestore The [FirebaseFirestore] instance for database operations.
 * @property auth The [FirebaseAuth] instance to access user authentication data.
 */
class FirebaseCommon(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {

    /**
     * Safely gets the cart collection reference. Returns null if the user is not authenticated.
     */
    private val cartCollection
        get() = auth.uid?.let { firestore.collection("user").document(it).collection("cart") }

    /**
     * Adds a [CartProduct] to the user's cart collection in Firestore.
     *
     * @param cartProduct The product object to be stored in the database.
     * @param onResult A callback function that returns the added [CartProduct] on success,
     * or an [Exception] on failure.
     */
    fun addProductToCart(cartProduct: CartProduct, onResult: (CartProduct?, Exception?) -> Unit ){
        val collection = cartCollection
        if (collection == null) {
            onResult(null, Exception("User is not authenticated (auth.uid is null)"))
            return
        }

        collection.document().set(cartProduct).addOnSuccessListener {
            onResult(cartProduct,null)
        }.addOnFailureListener {
            onResult(null,it)
        }
    }

    /**
     * Increases the quantity of a specific product in the cart using a Firestore transaction.
     * * @param documentId The unique ID of the document in the cart collection.
     * @param onResult A callback function that returns the [documentId] on success,
     * or an [Exception] on failure.
     */
    fun increaseQuantity(documentId: String, onResult: (String?, Exception?) -> Unit){
        val collection = cartCollection
        if (collection == null) {
            onResult(null, Exception("User is not authenticated (auth.uid is null)"))
            return
        }

        firestore.runTransaction { transaction ->
            val documentRef = collection.document(documentId)
            val document = transaction.get(documentRef)
            val productObject = document.toObject(CartProduct::class.java)
            productObject?.let { cartProduct ->
                val newQuantity = cartProduct.quantity + 1
                val newProductObject = cartProduct.copy(quantity = newQuantity)
                transaction.set(documentRef,newProductObject)
            }
        }.addOnSuccessListener {
            onResult(documentId,null)
        }.addOnFailureListener {
            onResult(null,it)
        }
    }

    /**
     * Decreases the quantity of a specific product in the cart using a Firestore transaction.
     * Prevents negative quantities by deleting the item if the count is 1.
     * * @param documentId The unique ID of the document to be updated.
     * @param onResult A callback function that returns the [documentId] on success.
     */
    fun decreaseQuantity(documentId: String, onResult: (String?, Exception?) -> Unit){
        val collection = cartCollection
        if (collection == null) {
            onResult(null, Exception("User is not authenticated (auth.uid is null)"))
            return
        }

        firestore.runTransaction { transaction ->
            val documentRef = collection.document(documentId)
            val document = transaction.get(documentRef)
            val productObject = document.toObject(CartProduct::class.java)
            productObject?.let { cartProduct ->
                if (cartProduct.quantity > 1) {
                    val newQuantity = cartProduct.quantity - 1
                    val newProductObject = cartProduct.copy(quantity = newQuantity)
                    transaction.set(documentRef,newProductObject)
                } else {
                    // If quantity is 1, delete the item from the cart instead of decreasing to 0
                    transaction.delete(documentRef)
                }
            }
        }.addOnSuccessListener {
            onResult(documentId,null)
        }.addOnFailureListener {
            onResult(null,it)
        }
    }

    /**
     * Represents the type of action to be performed on product quantity.
     */
    enum class QuantityChanging{
        INCREASE,
        DECREASE
    }
}