package com.example.thingapp.activities

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.thingapp.R
import com.example.thingapp.databinding.ActivityShoppingBinding
import com.example.thingapp.util.Resource
import com.example.thingapp.viewmodel.CartViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import androidx.core.content.ContextCompat


@AndroidEntryPoint
class ShoppingActivity : AppCompatActivity() {

    // Lazy initialization: binding is created only when it is first used
    val binding by lazy{
       ActivityShoppingBinding.inflate(layoutInflater)
    }

    /**
     * Shared ViewModel to observe cart data and update global UI elements like navigation badges.
     */
    val viewModel by viewModels<CartViewModel>()

    /**
     * Sets up the navigation controller and starts observing cart products to update the UI.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously
     * being shut down, this contains the most recent data.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // It is a component that responsible to navigate between the fragments, control the back stack of the fragments and all of those operations
        val navController = findNavController(R.id.shoppingHostFragment)
        binding.bottomNavigation.setupWithNavController(navController)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.cartProducts.collectLatest {
                    when(it){
                        is Resource.Success -> {
                            val count = it.data?.size ?: 0
                            val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)
                            bottomNavigation.getOrCreateBadge(R.id.cartFragment).apply {
                                number = count
                                backgroundColor = ContextCompat.getColor(this@ShoppingActivity, R.color.g_blue)
                            }
                        }
                        else -> Unit
                    }
                }
            }
        }

    }
}