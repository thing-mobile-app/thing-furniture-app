package com.example.thingapp.fragments.loginRegister

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.thingapp.R
import com.example.thingapp.activities.ShoppingActivity
import com.example.thingapp.databinding.FragmentLoginBinding
import com.example.thingapp.dialog.setupBottomSheetDialog
import com.example.thingapp.util.Resource
import com.example.thingapp.viewmodel.LoginViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {
    private lateinit var binding : FragmentLoginBinding
    private val viewModel : LoginViewModel by viewModels<LoginViewModel>()

    override  fun onCreateView(inflater : LayoutInflater, container : ViewGroup?, savedInstance : Bundle?): View {
        binding = FragmentLoginBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.dontHaveAnAccount.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.apply{
            buttonLoginLogin.setOnClickListener {
                val email = edEmailLogin.text.toString().trim()
                val password = edPasswordLogin.text.toString()
                viewModel.login(email, password)
            }
        }

        binding.forgotPasswordLogin.setOnClickListener {
            // calling our reset password dialog
            setupBottomSheetDialog {email ->
                viewModel.resetPassword(email)
            }
        }

        lifecycleScope.launch {
            viewModel.resetPassword.collect{
                when(it){
                    is Resource.Loading -> {

                    }
                    is Resource.Success -> {
                        Snackbar.make(requireView(),"Check your email for the password reset link.", Snackbar.LENGTH_LONG).show()
                    }
                    is Resource.Error -> {
                        Snackbar.make(requireView(),"Error ${it.message}", Snackbar.LENGTH_LONG).show()
                    }
                    else -> Unit
                }
            }
        }

        // coroutine scope to listen to that shared flow
        lifecycleScope.launch {
            viewModel.login.collect{
                when(it){
                    is Resource.Loading -> {
                        binding.buttonLoginLogin.startAnimation()
                    }
                    is Resource.Success -> {
                        binding.buttonLoginLogin.revertAnimation() // that could lead to a memory leak
                        Intent(requireActivity(), ShoppingActivity::class.java).also{ intent ->
                            // When the users log into their account they will navigate to the shopping activity and if they navigate back we don't need to go back to the login and register activity
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)

                        }
                    }
                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                        binding.buttonLoginLogin.revertAnimation()
                    }
                    else -> Unit
                }
            }

        }

    }
}