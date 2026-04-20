package com.example.thingapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thingapp.data.order.Order
import com.example.thingapp.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel responsible for processing and managing product orders.
 *
 * It coordinates Firestore [runBatch] operations to save order details across collections
 * and clear the user's cart simultaneously.
 *
 * @property firestore The [FirebaseFirestore] instance for database transactions.
 * @property auth The [FirebaseAuth] instance to identify the user placing the order.
 */
@HiltViewModel
class OrderViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
): ViewModel() {
    private val _order = MutableStateFlow<Resource<Order>>(Resource.Unspecified())

    /** StateFlow providing updates on the order placement status (Loading, Success, or Error). */
    val order = _order.asStateFlow()

    /**
     * Processes a purchase using a Firestore batch to ensure data consistency.
     *
     * Atomically performs three actions:
     * 1. Saves the order to the user's private orders.
     * 2. Saves the order to the global order collection.
     * 3. Clears the user's shopping cart.
     *
     * @param order The [Order] object containing purchase details.
     */
    fun placeOrder(order: Order) {
        val uid = auth.uid
        if (uid == null) {
            viewModelScope.launch {
                _order.emit(Resource.Error("User session expired."))
            }
            return
        }

        viewModelScope.launch {
            _order.emit(Resource.Loading())

            val userCartRef = firestore.collection("user").document(uid).collection("cart")
            val userOrdersRef = firestore.collection("user").document(uid).collection("orders").document()
            val globalOrdersRef = firestore.collection("orders").document()

            // Fetching the cart before starting the batch to avoid race conditions
            userCartRef.get().addOnSuccessListener { cartSnapshot ->

                firestore.runBatch { batch ->
                    // 1. Add order to user-specific collection
                    batch.set(userOrdersRef, order)

                    // 2. Add order to general orders collection
                    batch.set(globalOrdersRef, order)

                    // 3. Delete products from cart atomically
                    for (document in cartSnapshot.documents) {
                        batch.delete(document.reference)
                    }

                }.addOnSuccessListener {
                    viewModelScope.launch {
                        _order.emit(Resource.Success(order))
                    }
                }.addOnFailureListener { exception ->
                    viewModelScope.launch {
                        _order.emit(Resource.Error(exception.message.toString()))
                    }
                }

            }.addOnFailureListener { exception ->
                viewModelScope.launch {
                    _order.emit(Resource.Error("Failed to fetch cart: ${exception.message}"))
                }
            }
        }
    }
}