package com.example.thingapp.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.thingapp.data.Address
import com.example.thingapp.databinding.FragmentAddressBinding
import com.example.thingapp.util.Resource
import com.example.thingapp.viewmodel.AddressViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

/**
 * Fragment responsible for handling the user interface to add a new shipping/billing address.
 * It captures user inputs from the UI, communicates with [AddressViewModel] to save the data
 * to Firestore, and observes the state of the network request.
 */
@AndroidEntryPoint
class AddressFragment : Fragment() {

    private lateinit var binding: FragmentAddressBinding
    private val viewModel by viewModels<AddressViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddressBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Hide the delete button since we are adding a new address, not editing an existing one.
        binding.buttonDelelte.visibility = View.GONE

        setupClickListeners()
        observeAddressState()
    }

    /**
     * Initializes all UI interaction listeners, specifically for the save and close buttons.
     */
    private fun setupClickListeners() {
        // Navigate back to the previous screen when the close icon is clicked
        binding.imageAddressClose.setOnClickListener {
            findNavController().navigateUp()
        }

        // Collect all text field values and construct an Address object to trigger the save operation
        binding.buttonSave.setOnClickListener {
            val addressTitle = binding.edAddressTitle.text.toString()
            val fullName = binding.edFullName.text.toString()
            val street = binding.edStreet.text.toString()
            val phone = binding.edPhone.text.toString()
            val city = binding.edCity.text.toString()
            val state = binding.edState.text.toString()

            val address = Address(addressTitle, fullName, street, phone, city, state)

            // Pass the constructed address to the ViewModel to handle the Firestore operation
            viewModel.addAddress(address)
        }
    }

    /**
     * Subscribes to the ViewModel's state flow to listen for network request updates.
     * Updates the UI visibility (Progress Bar) and shows success/error messages accordingly.
     */
    private fun observeAddressState() {
        lifecycleScope.launchWhenStarted {
            viewModel.addNewAddress.collectLatest { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        // Show loading indicator when the save process starts
                        binding.progressbarAddress.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        // Hide loading indicator, show success message, and navigate back
                        binding.progressbarAddress.visibility = View.INVISIBLE
                        Toast.makeText(requireContext(), "Address saved successfully", Toast.LENGTH_SHORT).show()
                        findNavController().navigateUp()
                    }
                    is Resource.Error -> {
                        // Hide loading indicator and show the error message received from Firebase
                        binding.progressbarAddress.visibility = View.INVISIBLE
                        Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }
    }
}