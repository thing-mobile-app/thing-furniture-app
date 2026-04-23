package com.example.thingapp.fragments.settings

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import androidx.recyclerview.widget.RecyclerView
import com.example.thingapp.R
import com.example.thingapp.adapters.AllOrdersAdapter
import com.example.thingapp.data.order.Order
import com.example.thingapp.data.order.OrderStatus
import com.example.thingapp.databinding.FragmentOrdersBinding
import com.example.thingapp.util.Resource
import com.example.thingapp.viewmodel.AllOrdersViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Fragment that displays all or only active orders for the current user.
 *
 * Features:
 * - [activeOnly] = true → Track Orders mode: shows only Ordered/Confirmed/Shipped
 * - Search bar (Track Orders only): filters by order ID or date
 * - Delete: confirmation dialog + Firestore deletion for both modes
 */
@AndroidEntryPoint
class AllOrdersFragment : Fragment() {
    private lateinit var binding: FragmentOrdersBinding
    val viewModel by viewModels<AllOrdersViewModel>()
    val allOrdersAdapter by lazy { AllOrdersAdapter() }
    private val args by navArgs<AllOrdersFragmentArgs>()

    /** Full list kept for client-side search filtering */
    private var fullOrderList: List<Order> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrdersBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activeOnly = args.activeOnly

        // Dynamic toolbar title
        binding.tvOrdersTitle.text = if (activeOnly)
            getString(R.string.track_orders)
        else
            getString(R.string.all_orders)

        // Show search bar only in Track Orders mode
        if (activeOnly) {
            binding.etSearchOrders.visibility = View.VISIBLE
        }

        binding.imageCloseOrders.setOnClickListener {
            findNavController().navigateUp()
        }

        setupOrdersRv()
        observeOrders(activeOnly)
        observeDeleteResult()
        setupSearch()

        allOrdersAdapter.onClick = {
            val action = AllOrdersFragmentDirections.actionAllOrdersFragmentToOrderDetailFragment(it)
            findNavController().navigate(action)
        }

        allOrdersAdapter.onDeleteClick = { order ->
            showDeleteConfirmationDialog(order)
        }
    }

    private fun observeOrders(activeOnly: Boolean) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.allOrders.collectLatest {
                    when (it) {
                        is Resource.Loading -> {
                            binding.progressbarAllOrders.visibility = View.VISIBLE
                        }
                        is Resource.Success -> {
                            binding.progressbarAllOrders.visibility = View.GONE

                            val filtered = if (activeOnly) {
                                it.data?.filter { order ->
                                    order.orderStatus in listOf(
                                        OrderStatus.Ordered.status,
                                        OrderStatus.Confirmed.status,
                                        OrderStatus.Shipped.status
                                    )
                                } ?: emptyList()
                            } else {
                                it.data ?: emptyList()
                            }

                            fullOrderList = filtered
                            applySearchFilter(binding.etSearchOrders.text.toString())
                        }
                        is Resource.Error -> {
                            binding.progressbarAllOrders.visibility = View.GONE
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        }
                        else -> Unit
                    }
                }
            }
        }
    }

    private fun observeDeleteResult() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.deleteOrder.collectLatest {
                    when (it) {
                        is Resource.Loading -> { /* spinner already shown */ }
                        is Resource.Success -> {
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.order_deleted),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        is Resource.Error -> {
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        }
                        else -> Unit
                    }
                }
            }
        }
    }

    private fun setupSearch() {
        binding.etSearchOrders.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                applySearchFilter(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun applySearchFilter(query: String) {
        val result = if (query.isBlank()) {
            fullOrderList
        } else {
            fullOrderList.filter { order ->
                order.orderId.toString().contains(query, ignoreCase = true) ||
                order.date.contains(query, ignoreCase = true)
            }
        }
        allOrdersAdapter.differ.submitList(result)
        binding.tvEmptyOrders.visibility = if (result.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun showDeleteConfirmationDialog(order: Order) {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.delete_item))
            .setMessage(getString(R.string.order_delete_confirm))
            .setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                viewModel.deleteOrder(order)
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.cancel_dialog)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    /** Sets up the orders RecyclerView with adapter and layout manager. */
    private fun setupOrdersRv() {
        binding.rvAllOrders.apply {
            adapter = allOrdersAdapter
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        }
    }
}
