package com.example.thingapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import android.net.Uri
import android.provider.MediaStore
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import java.io.ByteArrayOutputStream
import java.util.UUID
import kotlinx.coroutines.tasks.await
import com.example.thingapp.ThingApplication
import com.example.thingapp.util.validateEmail
import com.example.thingapp.util.RegisterValidation
import com.example.thingapp.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.thingapp.data.User
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for [UserAccountFragment].
 * Handles fetching, updating, and uploading user profile information to Firebase Firestore and Storage.
 */
@HiltViewModel
class UserAccountViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val storage: StorageReference,
    app: Application
): AndroidViewModel(app) {

    private val _user = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val user = _user.asStateFlow()

    private val _updateInfo = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val updateInfo = _updateInfo.asStateFlow()

    init {
        getUser()
    }

    /**
     * Fetches the current user's information from Firestore.
     * Emits [Resource.Loading], then either [Resource.Success] or [Resource.Error].
     */
    fun getUser() {
        auth.uid?.let { uid ->
            viewModelScope.launch {
                _user.emit(Resource.Loading())
                firestore.collection(com.example.thingapp.util.Constants.USER_COLLECTION)
                    .document(uid)
                    .get()
                    .addOnSuccessListener {
                        val user = it.toObject(User::class.java)
                        user?.let {
                            viewModelScope.launch { _user.emit(Resource.Success(it)) }
                        } ?: viewModelScope.launch { _user.emit(Resource.Error("User information not found")) }
                    }
                    .addOnFailureListener {
                        viewModelScope.launch { _user.emit(Resource.Error(it.message.toString())) }
                    }
            }
        }
    }

    /**
     * Updates the user's account information, including an optional profile image.
     * @param user The [User] object with updated fields.
     * @param imageUri Optional [Uri] of the new profile image to upload.
     */
    fun updateUser(user: User, imageUri: Uri?) {
        val areInputsValid = validateEmail(user.email) is RegisterValidation.Success
                && user.firstName.trim().isNotEmpty()
                && user.lastName.trim().isNotEmpty()

        if (!areInputsValid) {
            viewModelScope.launch {
                _updateInfo.emit(Resource.Error("Check your inputs"))
            }
            return
        }

        viewModelScope.launch {
            _updateInfo.emit(Resource.Loading())
        }

        if (imageUri == null) {
            saveUserInformation(user, true)
        } else {
            saveUserInformationWithNewImage(user, imageUri)
        }
    }

    /**
     * Compresses and uploads a new profile image to Firebase Storage, then saves user info to Firestore.
     * @param user Current [User] data.
     * @param imageUri The new image [Uri].
     */
    private fun saveUserInformationWithNewImage(user: User, imageUri: Uri) {
        auth.uid?.let { uid ->
            viewModelScope.launch {
                try {
                    val imageBitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        val source = ImageDecoder.createSource(
                            getApplication<ThingApplication>().contentResolver,
                            imageUri
                        )
                        ImageDecoder.decodeBitmap(source)
                    } else {
                        MediaStore.Images.Media.getBitmap(
                            getApplication<ThingApplication>().contentResolver,
                            imageUri
                        )
                    }
                    val byteArrayOutputStream = ByteArrayOutputStream()
                    imageBitmap.compress(Bitmap.CompressFormat.JPEG, 96, byteArrayOutputStream)
                    val imageByteArray = byteArrayOutputStream.toByteArray()
                    val imageDirectory =
                        storage.child("profileImages/$uid/${UUID.randomUUID()}")
                    val result = imageDirectory.putBytes(imageByteArray).await()
                    val imageUrl = result.storage.downloadUrl.await().toString()
                    saveUserInformation(user.copy(imagePath = imageUrl), false)
                } catch (e: Exception) {
                    viewModelScope.launch {
                        _updateInfo.emit(Resource.Error(e.message.toString()))
                    }
                }
            }
        }
    }

    /**
     * Saves user details to Firestore using a transaction.
     * @param user The [User] data to save.
     * @param shouldRetrievedOldImage Whether to preserve the current image path if a new one isn't provided.
     */
    private fun saveUserInformation(user: User, shouldRetrievedOldImage: Boolean) {
        auth.uid?.let { uid ->
            firestore.runTransaction { transaction ->
                val documentRef = firestore.collection("user").document(uid)
                if (shouldRetrievedOldImage) {
                    val currentUser = transaction.get(documentRef).toObject(User::class.java)
                    val newUser = user.copy(imagePath = currentUser?.imagePath ?: "")
                    transaction.set(documentRef, newUser)
                } else {
                    transaction.set(documentRef, user)
                }
            }.addOnSuccessListener {
                viewModelScope.launch {
                    _updateInfo.emit(Resource.Success(user))
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _updateInfo.emit(Resource.Error(it.message.toString()))
                }
            }
        }
    }
}