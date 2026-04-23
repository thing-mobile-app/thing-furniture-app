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
import androidx.navigation.fragment.navArgs
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
    val args by navArgs<AddressFragmentArgs>()

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

        val address = args.address
        if(address == null){
            binding.buttonDelete.visibility = View.GONE 
        } else{
            binding.apply {
                edAddressTitle.setText(address.addressTitle)
                edFullName.setText(address.fullName)
                edStreet.setText(address.street)
                edPhone.setText(address.phone)
                edCity.setText(address.city)
                edState.setText(address.state)
                buttonDelete.visibility = View.VISIBLE
            }
        }

        setupClickListeners()
        observeAddressState()
    }

    private fun setupClickListeners() {
        binding.imageAddressClose.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.buttonSave.setOnClickListener {
            val title = binding.edAddressTitle.text.toString()
            val fullName = binding.edFullName.text.toString()
            val street = binding.edStreet.text.toString()
            val phone = binding.edPhone.text.toString()
            val city = binding.edCity.text.toString()
            val state = binding.edState.text.toString()

            val addressToSave = Address(
                id = args.address?.id ?: "", 
                addressTitle = title,
                fullName = fullName,
                street = street,
                phone = phone,
                city = city,
                state = state
            )
            viewModel.addAddress(addressToSave)
        }

        binding.buttonDelete.setOnClickListener {
            args.address?.let {
                viewModel.deleteAddress(it)
            }
        }
    }

    private fun observeAddressState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
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

                launch {
                    viewModel.deleteAddress.collectLatest { resource ->
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
}