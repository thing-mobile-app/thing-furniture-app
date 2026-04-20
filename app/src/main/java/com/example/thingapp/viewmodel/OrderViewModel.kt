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
     * Executes the order placement process.
     *
     * Saves the [order] to user-specific and global order collections, and clears
     * the user's shopping cart. Emits [Resource.Success] upon completion of all tasks.
     *
     * @param order The [Order] object containing product and shipping details.
     */
    fun placeHolder(order: Order) {
        viewModelScope.launch {
            _order.emit(Resource.Loading())
        }
        firestore.runBatch { batch ->
            // TODO: Add the order into user-orders collection
            // TODO: Add the order into orders collection
            // TODO: Delete the products from user-cart collection

            firestore.collection("user")
                .document(auth.uid!!)
                .collection("orders")
                .document()
                .set(order)

            firestore.collection("orders").document().set(order)

            firestore.collection("user").document(auth.uid!!).collection("cart").get()
                .addOnSuccessListener {
                    it.documents.forEach {
                        it.reference.delete()
                    }
                }

        }.addOnSuccessListener {
            viewModelScope.launch {
                _order.emit(Resource.Success(order))
            }
        }.addOnFailureListener {
            viewModelScope.launch {
                _order.emit(Resource.Error(it.message.toString()))
            }
        }

    }
}