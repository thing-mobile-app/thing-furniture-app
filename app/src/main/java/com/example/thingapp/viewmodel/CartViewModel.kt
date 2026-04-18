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
import kotlinx.coroutines.launch
import javax.inject.Inject

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

    init {
        getCartProducts()
    }

    /**
     * Subscribes to the user's cart collection in Firestore for real-time updates.
     * Updates [_cartProducts] and [cartProductDocuments] whenever the remote data changes.
     */
    private fun getCartProducts(){
        viewModelScope.launch { _cartProducts.emit(Resource.Unspecified()) }
        firestore.collection("user").document(auth.uid!!).collection("cart")
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
     * It identifies the document ID using the product's index and triggers the appropriate
     * update method based on the [quantityChanging] type.
     *
     * @param cartProduct The product whose quantity is being changed.
     * @param quantityChanging The direction of the change (INCREASE or DECREASE).
     */
    fun changeQuantity(
        cartProduct: CartProduct,
        quantityChanging: FirebaseCommon.QuantityChanging
    ){
        val index = cartProducts.value.data?.indexOf(cartProduct)

        /**
         * Index could be equal to -1 if the function [getCartProducts] delays which will also delay the result we except to be inside the [_cartProducts]
         * and to prevent the app from crashing we make a check.
         */

        if (index != null && index != -1) {
            val documentId = cartProductDocuments[index].id
            when(quantityChanging){
                FirebaseCommon.QuantityChanging.INCREASE ->{
                    increaseQuantity(documentId)
                }
                FirebaseCommon.QuantityChanging.DECREASE ->{
                    decreaseQuantity(documentId)
                }
            }
        }
    }

    /**
     * Triggers the decrement operation for a specific document via [firebaseCommon].
     * @param documentId The unique ID of the document in Firestore.
     */
    private fun decreaseQuantity(documentId: String) {
        firebaseCommon.decreaseQuantity(documentId){ result, exception ->
            if (exception!=null)
                viewModelScope.launch { _cartProducts.emit(Resource.Error(exception.message.toString())) }
        }
    }

    /**
     * Triggers the increment operation for a specific document via [firebaseCommon].
     * @param documentId The unique ID of the document in Firestore.
     */
    private fun increaseQuantity(documentId: String) {
        firebaseCommon.increaseQuantity(documentId){ result, exception ->
            if (exception!=null)
                viewModelScope.launch { _cartProducts.emit(Resource.Error(exception.message.toString())) }
        }
    }

}