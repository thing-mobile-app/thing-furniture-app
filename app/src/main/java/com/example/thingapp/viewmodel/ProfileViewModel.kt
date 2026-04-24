package com.example.thingapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thingapp.data.User
import com.example.thingapp.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel responsible for loading user profile data
 * and handling logout operations.
 */
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _user = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val user = _user.asStateFlow()
    private var snapshotListener: ListenerRegistration? = null

    init{
        getUser()
    }

    /**
     * Fetches the currently logged-in user's data from Firestore
     * and updates the user state flow.
     */
    fun getUser(){
        viewModelScope.launch {
            _user.emit(Resource.Loading())
        }

        val uid = auth.uid

        if (uid == null) {
            viewModelScope.launch {
                _user.emit(Resource.Error("User not logged in"))
            }
            return
        }

        firestore.collection("user").document(uid)
            .addSnapshotListener { value, error ->

                if (error != null) {
                    viewModelScope.launch {
                        _user.emit(Resource.Error(error.message.toString()))
                    }
                    return@addSnapshotListener
                }

                val user = value?.toObject(User::class.java)

                user?.let {
                    viewModelScope.launch {
                        _user.emit(Resource.Success(it))
                    }
                }
            }
    }

    /**
     * Signs out the current user from FirebaseAuth.
     */
    fun logout(){
        snapshotListener?.remove()
        auth.signOut()
    }

    override fun onCleared() {
        super.onCleared()
        snapshotListener?.remove()
    }
}