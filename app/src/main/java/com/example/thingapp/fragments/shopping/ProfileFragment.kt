package com.example.thingapp.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.thingapp.R
import com.example.thingapp.databinding.FragmentProfileBinding

/**
 * Fragment representing the user's profile screen.
 * Handles navigation to various user-specific sections like Addresses, Orders, etc.
 */
class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment using ViewBinding
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
    }

    /**
     * Initializes all UI interaction listeners for the profile screen.
     */
    private fun setupClickListeners() {
        /**
         * Listen for clicks on the "Addresses" section.
         * Navigates the user to the AddressFragment to add or view addresses.
         */
        binding.btnTestAddress.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_addressFragment)
        }
    }
}