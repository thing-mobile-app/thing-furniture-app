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
 * Handles adding, updating and deleting addresses in Firestore with safety checks.
 */
@HiltViewModel
class AddressViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _addNewAddress = MutableStateFlow<Resource<Address>>(Resource.Unspecified())
    val addNewAddress = _addNewAddress.asStateFlow()

    private val _deleteAddress = MutableStateFlow<Resource<Unit>>(Resource.Unspecified())
    val deleteAddress = _deleteAddress.asStateFlow()

    /**
     * Adds a new address or updates an existing one in the user's sub-collection in Firestore.
     * @param address The address data to be saved.
     */
    fun addAddress(address: Address) {
        if (validateInputs(address)) {
            viewModelScope.launch { _addNewAddress.emit(Resource.Loading()) }

            val userId = auth.uid ?: run {
                viewModelScope.launch { _addNewAddress.emit(Resource.Error("Authentication failed")) }
                return
            }

            val collection = firestore.collection("user").document(userId).collection("address")
            
            val document = if (address.id.isEmpty()) {
                collection.document() // New document
            } else {
                collection.document(address.id) // Existing document
            }

            val addressToSave = if (address.id.isEmpty()) {
                address.copy(id = document.id)
            } else {
                address
            }

            document.set(addressToSave)
                .addOnSuccessListener {
                    viewModelScope.launch { _addNewAddress.emit(Resource.Success(addressToSave)) }
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

    /**
     * Deletes an address from Firestore.
     * @param address The address to be deleted.
     */
    fun deleteAddress(address: Address) {
        val userId = auth.uid ?: return
        if (address.id.isEmpty()) return

        viewModelScope.launch { _deleteAddress.emit(Resource.Loading()) }

        firestore.collection("user").document(userId).collection("address").document(address.id)
            .delete()
            .addOnSuccessListener {
                viewModelScope.launch { _deleteAddress.emit(Resource.Success(Unit)) }
            }
            .addOnFailureListener { e ->
                viewModelScope.launch { _deleteAddress.emit(Resource.Error(e.message.toString())) }
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