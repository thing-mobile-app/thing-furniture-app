package com.example.thingapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thingapp.data.CartProduct
import com.example.thingapp.firebase.FirebaseCommon
import com.example.thingapp.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel responsible for managing product details and cart-related operations.
 *
 * This class interacts with Firestore to check for existing products in the user's cart
 * and coordinates with [FirebaseCommon] to perform add or update operations.
 *
 * @property firestore Instance of [FirebaseFirestore] to query the user's cart.
 * @property auth Instance of [FirebaseAuth] to retrieve the current user's unique ID.
 * @property firebaseCommon Utility class to execute standardized cart database operations.
 */
@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val firebaseCommon: FirebaseCommon
) : ViewModel() {

    private val _addToCart = MutableStateFlow<Resource<CartProduct>>(Resource.Unspecified())
    /**
     * StateFlow that emits the current status of the cart operation (Loading, Success, or Error).
     */
    val addToCart = _addToCart.asStateFlow()

    /**
     * Orchestrates the process of adding or updating a product in the cart.
     *
     * It first checks if the product already exists in the Firestore "cart" collection.
     * - If it doesn't exist, it calls [addNewProduct].
     * - If it exists and attributes (like color/size) match, it calls [increaseQuantity].
     * - If it exists but attributes differ, it treats it as a new entry via [addNewProduct].
     *
     * @param cartProduct The product information to be added or updated.
     */
    fun addUpdateProductInCart(cartProduct: CartProduct){
        viewModelScope.launch { _addToCart.emit(Resource.Loading()) }
        firestore.collection("user").document(auth.uid!!).collection("cart")
            .whereEqualTo("product.id",cartProduct.product.id).get()
            .addOnSuccessListener {
                it.documents.let {
                    if (it.isEmpty()){ // Add new product
                        addNewProduct(cartProduct)
                    }else{
                        val product = it.first().toObject(CartProduct::class.java)
                        if (product == cartProduct){ // Increase the quantity
                            val documentId =it.first().id
                            increaseQuantity(documentId,cartProduct)
                        } else { // Add new product
                            addNewProduct(cartProduct)
                        }
                    }
                }
            }.addOnFailureListener {
                viewModelScope.launch { _addToCart.emit(Resource.Error(it.message.toString())) }
            }
    }

    /**
     * Saves a new [CartProduct] entry to the database.
     *
     * @param cartProduct The new product object to save.
     */
    private fun addNewProduct(cartProduct: CartProduct){
        firebaseCommon.addProductToCart(cartProduct){ addedProduct, e ->
            viewModelScope.launch {
                if (e == null)
                    _addToCart.emit(Resource.Success(addedProduct!!))
                else
                    _addToCart.emit(Resource.Error(e.message.toString()))
            }
        }
    }

    /**
     * Increases the quantity of an existing product document.
     *
     * @param documentId The unique Firestore document ID of the item in the cart.
     * @param cartProduct The original product object to return in the [Resource.Success] state.
     */
    private fun increaseQuantity(documentId: String,cartProduct: CartProduct){
        firebaseCommon.increaseQuantity(documentId){ _, e ->
            viewModelScope.launch {
                if (e == null)
                    _addToCart.emit(Resource.Success(cartProduct))
                else
                    _addToCart.emit(Resource.Error(e.message.toString()))
            }
        }
    }

}