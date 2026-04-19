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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.thingapp.R
import com.example.thingapp.adapters.CartProductAdapter
import com.example.thingapp.databinding.FragmentCartBinding
import com.example.thingapp.util.Resource
import com.example.thingapp.viewmodel.CartViewModel
import android.app.AlertDialog
import com.example.thingapp.data.CartProduct
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

/**
 * Fragment responsible for managing the shopping cart UI and user interactions.
 *
 * This class observes the cart product list and dynamic total price from the [CartViewModel]
 * and handles quantity modifications, product deletion, and navigation.
 */
@AndroidEntryPoint
class CartFragment : Fragment(R.layout.fragment_cart) {

    private lateinit var binding: FragmentCartBinding
    private val cartAdapter by lazy { CartProductAdapter() }
    private val viewModel by viewModels<CartViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCartRv()
        observeCartProducts()
        observeTotalPrice()
        setupAdapterClickEvents()
    }

    /**
     * Observes the dynamic total price calculated in the ViewModel.
     * Recalculates automatically whenever the cart updates.
     */
    private fun observeTotalPrice() {
        lifecycleScope.launchWhenStarted {
            viewModel.totalPrice.collectLatest { price ->
                price?.let {
                    binding.tvTotalPrice.text = "$ ${String.format("%.2f", it)}"
                }
            }
        }
    }

    /**
     * Collects and observes the cart product list from [CartViewModel].
     * Manages UI states like Loading, Success, and Error.
     */
    private fun observeCartProducts() {
        lifecycleScope.launchWhenStarted {
            viewModel.cartProducts.collectLatest { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        binding.progressbarCart.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.progressbarCart.visibility = View.INVISIBLE
                        if (resource.data.isNullOrEmpty()) {
                            showEmptyCart()
                            cartAdapter.differ.submitList(emptyList())
                        } else {
                            hideEmptyCart()
                            cartAdapter.differ.submitList(resource.data)
                        }
                    }
                    is Resource.Error -> {
                        binding.progressbarCart.visibility = View.INVISIBLE
                        Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }
    }

    /**
     * Sets up listeners for user actions within the RecyclerView items.
     * Handles quantity changes with a safety dialog for item deletion.
     */
    private fun setupAdapterClickEvents() {
        // Increase quantity action
        cartAdapter.onPlusClick = { cartProduct ->
            viewModel.changeQuantity(cartProduct, com.example.thingapp.firebase.FirebaseCommon.QuantityChanging.INCREASE)
        }

        /**
         * Logic for decreasing quantity:
         * If $quantity > 1$, decrement the value.
         * If $quantity = 1$, prompt the user with a deletion confirmation dialog.
         */
        cartAdapter.onMinusClick = { cartProduct ->
            if (cartProduct.quantity <= 1) {
                showDeleteConfirmationDialog(cartProduct)
            } else {
                viewModel.changeQuantity(cartProduct, com.example.thingapp.firebase.FirebaseCommon.QuantityChanging.DECREASE)
            }
        }

        // Navigate to details screen
        cartAdapter.onProductClick = { cartProduct ->
            val b = Bundle().apply { putParcelable("product", cartProduct.product) }
            findNavController().navigate(R.id.action_cartFragment_to_productDetailsFragment, b)
        }
    }

    /**
     * Displays a native Android alert dialog to confirm item deletion.
     * @param cartProduct The product to be removed from the cart.
     */
    private fun showDeleteConfirmationDialog(cartProduct: CartProduct) {
        val alertDialog = AlertDialog.Builder(requireContext())
            .setTitle("Delete item from cart")
            .setMessage("Do you want to delete this item from your cart?")
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }
            .setPositiveButton("Yes") { dialog, _ ->
                viewModel.deleteCartProduct(cartProduct)
                dialog.dismiss()
            }
            .create()

        alertDialog.show()
    }

    /**
     * Updates UI components to show the empty cart placeholder.
     */
    private fun showEmptyCart() {
        binding.layoutCartEmpty.visibility = View.VISIBLE
        binding.totalBoxContainer.visibility = View.GONE
        binding.buttonCheckout.visibility = View.GONE
    }

    /**
     * Updates UI components to hide the placeholder and show cart details.
     */
    private fun hideEmptyCart() {
        binding.layoutCartEmpty.visibility = View.GONE
        binding.totalBoxContainer.visibility = View.VISIBLE
        binding.buttonCheckout.visibility = View.VISIBLE
    }

    /**
     * Configures the RecyclerView with a [LinearLayoutManager] and [cartAdapter].
     */
    private fun setupCartRv() {
        binding.rvCart.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = cartAdapter
        }
    }
}