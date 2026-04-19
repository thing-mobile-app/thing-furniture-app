package com.example.thingapp.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.thingapp.data.Address
import com.example.thingapp.databinding.FragmentAddressBinding
import com.example.thingapp.util.Resource
import com.example.thingapp.viewmodel.AddressViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * AddressFragment handles the user interface for adding or managing shipping addresses.
 * It integrates with AddressViewModel to persist data in Firebase Firestore.
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

        setupClickListeners()
        observeAddressState()
    }

    /**
     * Initializes click listeners for UI components.
     * Handles address saving, deletion (UI only), and navigation.
     */
    private fun setupClickListeners() {
        binding.imageAddressClose.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.buttonSave.setOnClickListener {
            val address = Address(
                binding.edAddressTitle.text.toString(),
                binding.edFullName.text.toString(),
                binding.edStreet.text.toString(),
                binding.edPhone.text.toString(),
                binding.edCity.text.toString(),
                binding.edState.text.toString()
            )
            viewModel.addAddress(address)
        }

        // Delete button is visible for UI consistency.
        // Backend logic will be implemented in the management phase.
        binding.buttonDelete.visibility = View.VISIBLE
        binding.buttonDelete.setOnClickListener {
            Toast.makeText(requireContext(), "Delete feature will be available soon", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Observes the StateFlow from ViewModel using a lifecycle-aware collector.
     * Replaces deprecated launchWhenStarted with repeatOnLifecycle for better resource management.
     */
    private fun observeAddressState() {
        viewLifecycleOwner.lifecycleScope.launch {
            // Collection starts only when the lifecycle is in STARTED state and stops in STOPPED.
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.addNewAddress.collectLatest { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            binding.progressbarAddress.visibility = View.VISIBLE
                        }
                        is Resource.Success -> {
                            binding.progressbarAddress.visibility = View.INVISIBLE
                            findNavController().navigateUp()
                        }
                        is Resource.Error -> {
                            binding.progressbarAddress.visibility = View.INVISIBLE
                            Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                        }
                        else -> Unit
                    }
                }
            }
        }
    }
}