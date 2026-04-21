package com.example.thingapp.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.thingapp.data.Category
import com.example.thingapp.viewmodel.CategoryViewModel
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Factory for creating CategoryViewModel instances with required dependencies.
 *
 * Provides FirebaseFirestore and selected Category to the ViewModel constructor.
 */
class BaseCategoryViewModelFactory(
    private val firestore: FirebaseFirestore,
    private val category: Category
): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CategoryViewModel(firestore, category) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }


}