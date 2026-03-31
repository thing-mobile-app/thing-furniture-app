package com.example.thingapp.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.thingapp.R
import com.example.thingapp.databinding.ActivityShoppingBinding


class ShoppingActivity : AppCompatActivity() {

    // Lazy initialization: binding is created only when it is first used
    val binding by lazy{
       ActivityShoppingBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // It is a component that responsible to navigate between the fragments, control the back stack of the fragments and all of those operations
        val navController = findNavController(R.id.shoppingHostFragment)
        binding.bottomNavigation.setupWithNavController(navController)

    }
}