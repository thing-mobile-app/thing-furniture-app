package com.example.thingapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thingapp.data.CartProduct
import com.example.thingapp.firebase.FirebaseCommon
import com.example.thingapp.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
// BU İMPORTU EKLEMEYİ UNUTMA:
import com.example.thingapp.helper.getProductPrice

/**
 * ViewModel responsible for managing the user's shopping cart items.
 *
 * This class provides real-time updates of the cart contents by listening to Firestore changes
 * and handles quantity modifications (increase/decrease) through [FirebaseCommon].
 *
 * @property firestore Instance of [FirebaseFirestore] to access the cart collection.
 * @property auth Instance of [FirebaseAuth] to identify the current user.
 * @property firebaseCommon Utility class to perform atomic operations on cart items.
 */
@HiltViewModel
class CartViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val firebaseCommon: FirebaseCommon
): ViewModel() {

    /**
     * Holds the list of [CartProduct] objects wrapped in a [Resource] to manage UI states.
     */
    private val _cartProducts = MutableStateFlow<Resource<List<CartProduct>>>(Resource.Unspecified())
    val cartProducts = _cartProducts.asStateFlow()

    /**
     * Local cache of Firestore [DocumentSnapshot]s to map data objects to their respective document IDs.
     */
    private var cartProductDocuments = emptyList<DocumentSnapshot>()

    /**
     * A StateFlow that transforms the list of cart products into a total price.
     * It automatically updates whenever the [_cartProducts] content changes.
     */
    val totalPrice = _cartProducts.map { resource ->
        resource.data?.let {
            calculatePrice(it)
        }
    }

    init {
        getCartProducts()
    }

    /**
     * Subscribes to the user's cart collection in Firestore for real-time updates.
     * Updates [_cartProducts] and [cartProductDocuments] whenever the remote data changes.
     */
    private fun getCartProducts(){
        val uid = auth.uid
        if (uid == null) {
            viewModelScope.launch { _cartProducts.emit(Resource.Error("User not authenticated")) }
            return
        }

        viewModelScope.launch { _cartProducts.emit(Resource.Unspecified()) }

        firestore.collection("user").document(uid).collection("cart")
            .addSnapshotListener { value, error ->
                if (error != null || value == null){
                    viewModelScope.launch { _cartProducts.emit(Resource.Error(error?.message.toString())) }
                } else {
                    cartProductDocuments = value.documents
                    val cartProducts = value.toObjects(CartProduct::class.java)
                    viewModelScope.launch { _cartProducts.emit(Resource.Success(cartProducts)) }
                }
            }
    }

    /**
     * Orchestrates the quantity change for a specific cart item.
     *
     * @param cartProduct The product whose quantity is being changed.
     * @param quantityChanging The direction of the change (INCREASE or DECREASE).
     */
    fun changeQuantity(
        cartProduct: CartProduct,
        quantityChanging: FirebaseCommon.QuantityChanging
    ){
        val index = cartProducts.value.data?.indexOf(cartProduct)

        if (index != null && index != -1) {
            val documentId = cartProductDocuments[index].id
            when(quantityChanging){
                FirebaseCommon.QuantityChanging.INCREASE -> {
                    increaseQuantity(documentId)
                }
                FirebaseCommon.QuantityChanging.DECREASE -> {
                    decreaseQuantity(documentId)
                }
            }
        }
    }

    private fun decreaseQuantity(documentId: String) {
        firebaseCommon.decreaseQuantity(documentId){ _, exception ->
            if (exception != null)
                viewModelScope.launch { _cartProducts.emit(Resource.Error(exception.message.toString())) }
        }
    }

    private fun increaseQuantity(documentId: String) {
        firebaseCommon.increaseQuantity(documentId){ _, exception ->
            if (exception != null)
                viewModelScope.launch { _cartProducts.emit(Resource.Error(exception.message.toString())) }
        }
    }

    /**
     * Calculates the total price of all items in the cart, considering discounts.
     * Formula: $$ P = \sum_{i=1}^{n} (Price_{discounted, i} \times Quantity_i) $$
     *
     * @param data The list of products currently in the user's cart.
     * @return The calculated total price as a Float.
     */
    private fun calculatePrice(data: List<CartProduct>): Float {
        return data.sumOf { cartProduct ->
            val priceAfterOffer = cartProduct.product.offerPercentage.getProductPrice(cartProduct.product.price)
            (priceAfterOffer * cartProduct.quantity).toDouble()
        }.toFloat()
    }

    /**
     * Deletes a specific product from the user's cart in Firestore.
     * Uses a safe UID check to prevent NullPointerException.
     *
     * @param cartProduct The product object to be removed.
     */
    fun deleteCartProduct(cartProduct: CartProduct) {
        val uid = auth.uid
        val index = _cartProducts.value.data?.indexOf(cartProduct)

        if (uid != null && index != null && index != -1) {
            val documentId = cartProductDocuments[index].id
            firestore.collection("user").document(uid).collection("cart")
                .document(documentId).delete()
        }
    }
}