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
 * ViewModel responsible for managing address-related operations.
 * Handles adding new addresses to Firestore with safety checks.
 */
@HiltViewModel
class AddressViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _addNewAddress = MutableStateFlow<Resource<Address>>(Resource.Unspecified())
    val addNewAddress = _addNewAddress.asStateFlow()

    /**
     * Adds a new address to the user's sub-collection in Firestore.
     * @param address The address data to be saved.
     */
    fun addAddress(address: Address) {
        if (validateInputs(address)) {
            viewModelScope.launch { _addNewAddress.emit(Resource.Loading()) }

            // Review Fix: Avoid using '!!' by providing a safe fallback or early return
            val userId = auth.uid ?: run {
                viewModelScope.launch { _addNewAddress.emit(Resource.Error("Authentication failed")) }
                return
            }

            firestore.collection("user").document(userId).collection("address").document()
                .set(address)
                .addOnSuccessListener {
                    viewModelScope.launch { _addNewAddress.emit(Resource.Success(address)) }
                }
                .addOnFailureListener { e ->
                    viewModelScope.launch { _addNewAddress.emit(Resource.Error(e.message.toString())) }
                }
        } else {
            viewModelScope.launch {
                _addNewAddress.emit(Resource.Error("Please fill all fields correctly"))
            }
        }
    }

    private fun validateInputs(address: Address): Boolean {
        return address.addressTitle.trim().isNotEmpty() &&
                address.fullName.trim().isNotEmpty() &&
                address.street.trim().isNotEmpty() &&
                address.phone.trim().isNotEmpty() &&
                address.city.trim().isNotEmpty() &&
                address.state.trim().isNotEmpty()
    }
}