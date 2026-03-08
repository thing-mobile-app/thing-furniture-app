package com.example.thingapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.thingapp.data.User
import com.example.thingapp.util.Constants.USER_COLLECTION
import com.example.thingapp.util.RegisterFieldsState
import com.example.thingapp.util.RegisterValidation
import com.example.thingapp.util.Resource
import com.example.thingapp.util.validateEmail
import com.example.thingapp.util.validatePassword
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
// Hilt injects FirebaseAuth automatically when ViewModel is created
class RegisterViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val fireStore : FirebaseFirestore
) : ViewModel() {

    // Private mutable state inside ViewModel
    private val _register = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    // Read only flow outside ViewModel (UI)
    val register : Flow<Resource<User>> = _register

    private val _validation = Channel<RegisterFieldsState>()
    val validation = _validation.receiveAsFlow()

    // Creating a new account in Firebase Auth
    fun createAccountWithEmailAndPassword(user : User, password : String){
        if(checkValidation(user, password)){
            runBlocking {
                _register.emit(Resource.Loading())
            }
            firebaseAuth.createUserWithEmailAndPassword(user.email, password)
                // if everything is successful
                .addOnSuccessListener {
                    it.user?.let{
                        saveUserInfo(it.uid, user)

                    }
                }
                // if something goes wrong
                .addOnFailureListener {
                    _register.value = Resource.Error(it.message.toString())
                }
        }
        else {
            val registerFieldsState = RegisterFieldsState(validateEmail(user.email), validatePassword(password))

            runBlocking {
                _validation.send(registerFieldsState)
            }
        }

    }


    private fun saveUserInfo(userUid : String, user : User){
        fireStore.collection(USER_COLLECTION).document(userUid).set(user)
            .addOnSuccessListener {
                _register.value = Resource.Success(user)
            }
            .addOnFailureListener {
                _register.value = Resource.Error(it.message.toString())
            }
    }

    private fun checkValidation(user: User, password: String) : Boolean {
        val emailValidation = validateEmail(user.email)
        val passwordValidation = validatePassword(password)
        val shouldRegister =
            emailValidation is RegisterValidation.Success && passwordValidation is RegisterValidation.Success

        return shouldRegister
    }
}