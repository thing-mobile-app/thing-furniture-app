package com.example.thingapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.thingapp.data.User
import com.example.thingapp.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
// Hilt injects FirebaseAuth automatically when ViewModel is created
class LoginRegisterViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    // Private mutable state inside ViewModel
    private val _register = MutableStateFlow<Resource<FirebaseUser>>(Resource.Loading())
    // Read only flow outside ViewModel (UI)
    val register : Flow<Resource<FirebaseUser>> = _register


    // Creating a new account in Firebase Auth
    fun createAccountWithEmailAndPassword(user : User, password : String){
        firebaseAuth.createUserWithEmailAndPassword(user.email, password)
            // if everything is successful
            .addOnSuccessListener {
                it.user?.let{
                    _register.value = Resource.Success(it)
                }
            }
            // if something goes wrong
            .addOnFailureListener {
                _register.value = Resource.Error(it.message.toString())
            }
    }
}