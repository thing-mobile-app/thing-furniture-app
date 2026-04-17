package com.example.thingapp.di

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import android.app.Application
import android.content.Context.MODE_PRIVATE
import com.example.thingapp.firebase.FirebaseCommon
import com.example.thingapp.util.Constants.INTRODUCTION_SP
import com.google.firebase.firestore.FirebaseFirestore

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

    @Provides
    @Singleton
    // Provide Shared Preferences for Introduction flow
    fun provideIntroductionSP(
        application: Application
    ) = application.getSharedPreferences(INTRODUCTION_SP, MODE_PRIVATE)

    @Provides
    @Singleton
    fun provideFirebaseCommon(
        firebaseAuth: FirebaseAuth,
        firestore: FirebaseFirestore
    ) = FirebaseCommon(firestore,firebaseAuth)

}