package com.example.thingapp.fragments.shopping

import android.app.AlertDialog
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.thingapp.R
import com.example.thingapp.adapters.AddressAdapter
import com.example.thingapp.adapters.BillingProductsAdapter
import com.example.thingapp.data.Address
import com.example.thingapp.data.order.Order
import com.example.thingapp.data.order.OrderStatus
import com.example.thingapp.databinding.FragmentBillingBinding
import com.example.thingapp.util.Resource
import com.example.thingapp.viewmodel.BillingViewModel
import com.example.thingapp.viewmodel.OrderViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Fragment responsible for displaying the billing and checkout screen.
 * It handles user address selection, displays the order summary (cart items),
 * and manages the final steps before placing an order.
 */
@AndroidEntryPoint
class BillingFragment : Fragment() {
    private lateinit var binding: FragmentBillingBinding

    // Retrieve arguments passed via Navigation Component (Safe Args)
    private val args by navArgs<BillingFragmentArgs>()

    // Initialize adapters lazily for better memory management
    private val billingProductsAdapter by lazy { BillingProductsAdapter() }
    private val addressAdapter by lazy { AddressAdapter() }

    private val billingViewModel by viewModels<BillingViewModel>()
    private var selectedAddress: Address?=null
    private val orderViewModel by viewModels<OrderViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBillingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAddressRecyclerView()
        setupBillingProductsRecyclerView()
        observeAddressState()
        observeOrderState()

        // Bind the data received from CartFragment to the UI
        binding.tvTotalPrice.text = "$ ${String.format("%.2f", args.totalPrice)}"
        billingProductsAdapter.differ.submitList(args.products.toList())

        // Handle back navigation
        binding.imageCloseBilling.setOnClickListener {
            findNavController().navigateUp()
        }

        // Navigate to AddressFragment when the "+" icon is clicked
        binding.imageAddAddress.setOnClickListener {
            findNavController().navigate(R.id.action_billingFragment_to_addressFragment)
        }

        binding.buttonPlaceOrder.setOnClickListener {
            if(selectedAddress == null){
                Toast.makeText(requireContext(),"Please select an address", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            showOrderConfirmationDialog()
        }

        // Capture the selected address for final order placement
        addressAdapter.onClick = { address ->
            selectedAddress = address
            Toast.makeText(
                requireContext(),
                "Selected: ${address.addressTitle}",
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    /**
     * Displays an [AlertDialog] to confirm the purchase.
     *
     * If confirmed, it creates an [Order] object with the selected address
     * and triggers [orderViewModel.placeHolder].
     */
    private fun showOrderConfirmationDialog() {
        val address = selectedAddress ?: return

        val order = Order(
            orderStatus = OrderStatus.Ordered.status,
            totalPrice = args.totalPrice,
            products = args.products.toList(),
            address = address
        )

        AlertDialog.Builder(requireContext())
            .setTitle("Order items")
            .setMessage("Do you want to order your cart items?")
            .setPositiveButton("Confirm") { dialog, _ ->
                orderViewModel.placeHolder(order)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    /**
     * Initializes the RecyclerView for displaying saved user addresses horizontally.
     */
    private fun setupAddressRecyclerView() {
        binding.rvAddresses.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = addressAdapter
        }
    }

    /**
     * Initializes the RecyclerView for displaying the order summary (cart items) horizontally.
     */
    private fun setupBillingProductsRecyclerView() {
        binding.rvProducts.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = billingProductsAdapter
        }
    }

    /**
     * Observes the address state from the ViewModel and updates the adapter accordingly.
     */
    private fun observeAddressState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                billingViewModel.address.collectLatest { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            // Optionally handle loading state (e.g., show a progress bar)
                        }
                        is Resource.Success -> {
                            addressAdapter.differ.submitList(resource.data)
                        }
                        is Resource.Error -> {
                            Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                        }
                        else -> Unit
                    }
                }
            }
        }
    }

    /**
     * Observes order placement status and updates button state and navigation.
     *
     * - **Loading:** Disables button and shows progress text.
     * - **Success:** Resets button, shows [Snackbar], and navigates back.
     * - **Error:** Resets button and shows [Toast] with the error.
     */
    private fun observeOrderState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                orderViewModel.order.collectLatest { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            binding.buttonPlaceOrder.isEnabled = false
                            binding.buttonPlaceOrder.text = "Placing..."
                        }
                        is Resource.Success -> {
                            binding.buttonPlaceOrder.isEnabled = true
                            binding.buttonPlaceOrder.text = "Place Order"
                            Snackbar.make(
                                requireView(),
                                "Your order was placed",
                                Snackbar.LENGTH_SHORT
                            ).show()
                            findNavController().navigateUp()
                        }
                        is Resource.Error -> {
                            binding.buttonPlaceOrder.isEnabled = true
                            binding.buttonPlaceOrder.text = "Place Order"
                            Toast.makeText(
                                requireContext(),
                                "Error ${resource.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        else -> Unit
                    }
                }
            }
        }
    }


}