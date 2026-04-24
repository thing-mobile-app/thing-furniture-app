package com.example.thingapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thingapp.data.Address
import com.example.thingapp.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing data flow in the Billing Fragment.
 * Fetches user-specific addresses and cart items from Firestore.
 */
@HiltViewModel
class BillingViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _address = MutableStateFlow<Resource<List<Address>>>(Resource.Unspecified())
    val address = _address.asStateFlow()

    init {
        getUserAddresses()
    }

    /**
     * Retrieves the list of addresses for the currently authenticated user.
     * Maps Firestore document IDs to the Address object to ensure update/delete operations work.
     */
    fun getUserAddresses() {
        val userId = auth.uid ?: run {
            viewModelScope.launch { _address.emit(Resource.Error("User not authenticated")) }
            return
        }

        viewModelScope.launch { _address.emit(Resource.Loading()) }

        firestore.collection("user").document(userId).collection("address")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    viewModelScope.launch { _address.emit(Resource.Error(error.message.toString())) }
                    return@addSnapshotListener
                }

                // Map documents manually to ensure the 'id' field in the data class matches the Firestore document ID
                val addressList = value?.documents?.mapNotNull { doc ->
                    doc.toObject(Address::class.java)?.copy(id = doc.id)
                } ?: emptyList()

                viewModelScope.launch { _address.emit(Resource.Success(addressList)) }
            }
    }
}