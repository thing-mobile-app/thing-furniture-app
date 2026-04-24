package com.example.thingapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thingapp.data.CartProduct
import com.example.thingapp.firebase.FirebaseCommon
import com.example.thingapp.helper.getProductPrice
import com.example.thingapp.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel responsible for managing the user's shopping cart items and price calculations.
 * Provides real-time updates from Firestore and manages state through StateFlows.
 */
@HiltViewModel
class CartViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val firebaseCommon: FirebaseCommon
) : ViewModel() {

    private val _cartProducts = MutableStateFlow<Resource<List<CartProduct>>>(Resource.Unspecified())
    val cartProducts = _cartProducts.asStateFlow()

    private var cartProductDocuments = emptyList<DocumentSnapshot>()

    /**
     * A StateFlow that dynamically calculates the total price of the cart.
     * It maps the [cartProducts] state and uses the [getProductPrice] helper
     * to ensure discounts are correctly applied in the final sum.
     */
    val totalPrice = cartProducts.map { resource ->
        resource.data?.let { products ->
            calculatePrice(products)
        } ?: 0f
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0f)

    init {
        getCartProducts()
    }

    private fun getCartProducts() {
        val uid = auth.uid
        if (uid == null) {
            viewModelScope.launch { _cartProducts.emit(Resource.Error("User not authenticated")) }
            return
        }

        viewModelScope.launch { _cartProducts.emit(Resource.Unspecified()) }

        firestore.collection("user").document(uid).collection("cart")
            .addSnapshotListener { value, error ->
                if (error != null || value == null) {
                    viewModelScope.launch { _cartProducts.emit(Resource.Error(error?.message.toString())) }
                } else {
                    cartProductDocuments = value.documents
                    val products = value.toObjects(CartProduct::class.java)
                    viewModelScope.launch { _cartProducts.emit(Resource.Success(products)) }
                }
            }
    }

    fun changeQuantity(
        cartProduct: CartProduct,
        quantityChanging: FirebaseCommon.QuantityChanging
    ) {
        val index = cartProducts.value.data?.indexOf(cartProduct)
        if (index != null && index != -1) {
            val documentId = cartProductDocuments[index].id
            when (quantityChanging) {
                FirebaseCommon.QuantityChanging.INCREASE -> increaseQuantity(documentId)
                FirebaseCommon.QuantityChanging.DECREASE -> decreaseQuantity(documentId)
            }
        }
    }

    private fun decreaseQuantity(documentId: String) {
        firebaseCommon.decreaseQuantity(documentId) { _, exception ->
            if (exception != null)
                viewModelScope.launch { _cartProducts.emit(Resource.Error(exception.message.toString())) }
        }
    }

    private fun increaseQuantity(documentId: String) {
        firebaseCommon.increaseQuantity(documentId) { _, exception ->
            if (exception != null)
                viewModelScope.launch { _cartProducts.emit(Resource.Error(exception.message.toString())) }
        }
    }

    /**
     * Helper to calculate the sum of products considering their discount percentages.
     */
    private fun calculatePrice(data: List<CartProduct>): Float {
        return data.sumOf { cartProduct ->
            val priceAfterOffer = cartProduct.product.offerPercentage.getProductPrice(cartProduct.product.price)
            (priceAfterOffer * cartProduct.quantity).toDouble()
        }.toFloat()
    }

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