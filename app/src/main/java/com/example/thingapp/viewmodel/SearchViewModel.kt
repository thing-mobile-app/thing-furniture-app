package com.example.thingapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thingapp.data.Product
import com.example.thingapp.util.Resource
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _search = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val search: StateFlow<Resource<List<Product>>> = _search

    fun searchProducts(searchQuery: String) {
        val query = searchQuery.trim()
        if (query.isEmpty()) {
            viewModelScope.launch {
                _search.emit(Resource.Unspecified())
            }
            return
        }

        viewModelScope.launch {
            _search.emit(Resource.Loading())
        }

        // Firestore search is case-sensitive. We try to match the query as is, 
        // and also with the first letter capitalized as it's common for product names.
        val capitalizedQuery = query.replaceFirstChar { it.uppercase() }

        firestore.collection("Products")
            .whereGreaterThanOrEqualTo("name", capitalizedQuery)
            .whereLessThanOrEqualTo("name", capitalizedQuery + "\uf8ff")
            .get()
            .addOnSuccessListener { result ->
                val products = result.toObjects(Product::class.java)
                viewModelScope.launch {
                    _search.emit(Resource.Success(products))
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _search.emit(Resource.Error(it.message.toString()))
                }
            }
    }
}