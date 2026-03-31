package com.example.thingapp.fragments.loginRegister

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.thingapp.R
import com.example.thingapp.databinding.FragmentIntroductionBinding
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.thingapp.viewmodel.IntroductionViewModel
import dagger.hilt.android.AndroidEntryPoint
import android.content.Intent
import com.example.thingapp.activities.ShoppingActivity
import com.example.thingapp.viewmodel.IntroductionViewModel.Companion.ACCOUNT_OPTIONS_FRAGMENT
import com.example.thingapp.viewmodel.IntroductionViewModel.Companion.SHOPPING_ACTIVITY

@AndroidEntryPoint
class IntroductionFragment : Fragment(R.layout.fragment_introduction) {
    private  lateinit var binding : FragmentIntroductionBinding
    // Delegate for lazy initialization of IntroductionViewModel
    private val viewModel by viewModels<IntroductionViewModel>()

    override fun onCreateView(
        inflater : LayoutInflater,
        container : ViewGroup?,
        saveInstanceState : Bundle?
    ) : View{
        binding = FragmentIntroductionBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observe navigation flow from the ViewModel and react on changes
        lifecycleScope.launchWhenStarted {
            viewModel.navigate.collect {
                when(it){
                    SHOPPING_ACTIVITY -> {
                         // Go to Shopping Activity
                         Intent(requireActivity(), ShoppingActivity::class.java).also{ intent ->
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)

                        }
                    }

                    ACCOUNT_OPTIONS_FRAGMENT -> {
                            // Go to Account Options Screen
                            findNavController().navigate(it) 
                    }

                    else -> Unit
                }
            }
        }

        binding.buttonStart.setOnClickListener{
            // Update status and navigate
            viewModel.startButtonClick()
            findNavController().navigate(R.id.action_introductionFragment_to_accountOptionsFragment)
        }
    }
}