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
 * ViewModel responsible for handling address operations.
 * It takes the user's input and securely saves it to Firestore under the user's profile.
 */
@HiltViewModel
class AddressViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
): ViewModel() {

    private val _addNewAddress = MutableStateFlow<Resource<Address>>(Resource.Unspecified())
    val addNewAddress = _addNewAddress.asStateFlow()

    /**
     * Saves a new address to the Firestore database.
     * @param address The [Address] data object containing user inputs.
     */
    fun addAddress(address: Address) {
        val validateInputs = validateInputs(address)

        if (validateInputs) {
            viewModelScope.launch { _addNewAddress.emit(Resource.Loading()) }

            // Kullanıcının kendi dokümanının altına "address" koleksiyonu açıp kaydediyoruz
            firestore.collection("user").document(auth.uid!!).collection("address").document()
                .set(address)
                .addOnSuccessListener {
                    viewModelScope.launch { _addNewAddress.emit(Resource.Success(address)) }
                }
                .addOnFailureListener {
                    viewModelScope.launch { _addNewAddress.emit(Resource.Error(it.message.toString())) }
                }
        } else {
            viewModelScope.launch {
                _addNewAddress.emit(Resource.Error("All fields are required"))
            }
        }
    }

    /**
     * Validates if all the required address fields are filled.
     */
    private fun validateInputs(address: Address): Boolean {
        return address.addressTitle.trim().isNotEmpty() &&
                address.city.trim().isNotEmpty() &&
                address.phone.trim().isNotEmpty() &&
                address.state.trim().isNotEmpty() &&
                address.fullName.trim().isNotEmpty() &&
                address.street.trim().isNotEmpty()
    }
}