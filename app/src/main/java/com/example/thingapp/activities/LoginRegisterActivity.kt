package com.example.thingapp.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.thingapp.R
import dagger.hilt.android.AndroidEntryPoint
import androidx.activity.addCallback

/**
 * Activity that hosts login and register fragments.
 * Closes the app when back button is pressed.
 */
@AndroidEntryPoint
class LoginRegisterActivity : AppCompatActivity() {
    /**
     * Initializes the login/register screen
     * and custom back press behavior.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_register)

        onBackPressedDispatcher.addCallback(this) {
            finishAffinity()
        }
    }
}