package com.example.thingapp.di

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
// All the dependencies inside this module stay alive as long as app is alive
@InstallIn(SingletonComponent::class)
object AppModule {


    // One instance throughout the whole app (Singleton)
    @Provides
    @Singleton
    fun provideFirebaseAuth() = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseStoreDatabase() = Firebase.firestore
}