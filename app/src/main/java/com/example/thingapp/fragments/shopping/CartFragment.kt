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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.thingapp.adapters.CartProductAdapter
import com.example.thingapp.data.CartProduct
import com.example.thingapp.databinding.FragmentCartBinding
import com.example.thingapp.firebase.FirebaseCommon
import com.example.thingapp.util.Resource
import com.example.thingapp.viewmodel.CartViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.example.thingapp.R

/**
 * Fragment that displays the user's shopping cart.
 * Handles item interactions and transitions to the billing process.
 */
@AndroidEntryPoint
class CartFragment : Fragment() {

    private lateinit var binding: FragmentCartBinding
    private val cartAdapter by lazy { CartProductAdapter() }
    private val viewModel by viewModels<CartViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCartRecyclerView()
        observeCartProducts()
        observeTotalPrice()
        setupAdapterClickEvents()

        binding.imageCloseCart.setOnClickListener {
            findNavController().navigateUp()
        }

        // Handle navigation to Billing screen with current data
        binding.buttonCheckout.setOnClickListener {
            val products = cartAdapter.differ.currentList.toTypedArray()
            val totalPrice = viewModel.totalPrice.value // Now correctly works as a StateFlow snapshot

            // We passed true because this is going to be a payment
            val action = CartFragmentDirections.actionCartFragmentToBillingFragment(products, totalPrice, true)
            findNavController().navigate(action)
        }
    }

    private fun observeTotalPrice() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.totalPrice.collectLatest { price ->
                    binding.tvTotalPrice.text = "$ ${String.format("%.2f", price)}"
                }
            }
        }
    }

    private fun observeCartProducts() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.cartProducts.collectLatest { resource ->
                    when (resource) {
                        is Resource.Loading -> binding.progressbarCart.visibility = View.VISIBLE
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
    }

    private fun setupAdapterClickEvents() {
        cartAdapter.onPlusClick = { product ->
            viewModel.changeQuantity(product, FirebaseCommon.QuantityChanging.INCREASE)
        }

        cartAdapter.onMinusClick = { product ->
            if (product.quantity <= 1) showDeleteConfirmationDialog(product)
            else viewModel.changeQuantity(product, FirebaseCommon.QuantityChanging.DECREASE)
        }

        cartAdapter.onProductClick = { product ->
            val action = CartFragmentDirections.actionCartFragmentToProductDetailsFragment(
                product.product,
                cartProductToEdit = product
            )
            findNavController().navigate(action)
        }
    }

    private fun showDeleteConfirmationDialog(cartProduct: CartProduct) {
        AlertDialog.Builder(requireContext()).apply {
            setTitle(getString(R.string.delete_item))
            setMessage(getString(R.string.delete_item_confirm))
            setNegativeButton(getString(R.string.cancel_dialog)) { dialog, _ -> dialog.dismiss() }
            setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                viewModel.deleteCartProduct(cartProduct)
                dialog.dismiss()
            }
            create().show()
        }
    }

    private fun showEmptyCart() {
        binding.apply {
            layoutCartEmpty.visibility = View.VISIBLE
            totalBoxContainer.visibility = View.GONE
            buttonCheckout.visibility = View.GONE
        }
    }

    private fun hideEmptyCart() {
        binding.apply {
            layoutCartEmpty.visibility = View.GONE
            totalBoxContainer.visibility = View.VISIBLE
            buttonCheckout.visibility = View.VISIBLE
        }
    }

    private fun setupCartRecyclerView() {
        binding.rvCart.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = cartAdapter
        }
    }
}