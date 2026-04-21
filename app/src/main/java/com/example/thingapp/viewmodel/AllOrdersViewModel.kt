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
 * ViewModel that fetches and exposes all orders for the current user from Firestore.
 */
@HiltViewModel
class AllOrdersViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
): ViewModel() {

    private val _allOrders = MutableStateFlow<Resource<List<Order>>>(Resource.Unspecified())

    /** StateFlow emitting the current state of the orders list. */
    val allOrders = _allOrders.asStateFlow()

    init {
        getAllOrders()
    }

    /**
     * Fetches all orders from the current user's Firestore "orders" sub-collection.
     * Emits [Resource.Loading], then [Resource.Success] or [Resource.Error].
     */
    fun getAllOrders() {
        viewModelScope.launch {
            _allOrders.emit(Resource.Loading())
        }

        auth.uid?.let { uid ->
            firestore.collection("user").document(uid).collection("orders").get()
                .addOnSuccessListener {
                    val orders = it.toObjects(Order::class.java)
                    viewModelScope.launch {
                        _allOrders.emit(Resource.Success(orders))
                    }
                }.addOnFailureListener {
                    viewModelScope.launch {
                        _allOrders.emit(Resource.Error(it.message.toString()))
                    }
                }
        } ?: viewModelScope.launch {
            _allOrders.emit(Resource.Error("User not authenticated"))
        }
    }
}
