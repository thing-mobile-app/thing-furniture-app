package com.example.thingapp.util

import android.view.View
import androidx.fragment.app.Fragment
import com.example.thingapp.R
import com.example.thingapp.activities.ShoppingActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

// Function to hide the bottom navigation view
fun Fragment.hideBottomNavigationView(){
    val bottomNavigationView =
        (activity as ShoppingActivity).findViewById<BottomNavigationView>(
            R.id.bottomNavigation
        )
    bottomNavigationView.visibility = View.GONE
}

// Function to show the bottom navigation view
fun Fragment.showBottomNavigationView(){
    val bottomNavigationView =
        (activity as ShoppingActivity).findViewById<BottomNavigationView>(
            R.id.bottomNavigation
        )
    bottomNavigationView.visibility = View.VISIBLE
}
