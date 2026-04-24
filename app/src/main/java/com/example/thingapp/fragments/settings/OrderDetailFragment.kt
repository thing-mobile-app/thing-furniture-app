package com.example.thingapp.fragments.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.thingapp.adapters.BillingProductsAdapter
import com.example.thingapp.data.order.OrderStatus
import com.example.thingapp.data.order.getOrderStatus
import com.example.thingapp.databinding.FragmentOrderDetailBinding
import com.example.thingapp.R
import com.example.thingapp.util.VerticalItemDecoration

/**
 * Displays the details of a selected order.
 *
 * This fragment shows:
 * - Order number
 * - Delivery progress using step view
 * - Customer address information
 * - Total price
 * - Ordered product list
 *
 * The selected order is received through Safe Args navigation.
 */
class OrderDetailFragment : Fragment() {
    private lateinit var binding : FragmentOrderDetailBinding
    private val billingProductsAdapter by lazy{ BillingProductsAdapter() }
    private val args by navArgs<OrderDetailFragmentArgs>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderDetailBinding.inflate(inflater)
        return binding.root
    }



    /**
     * Called after the fragment view has been created.
     *
     * Initializes RecyclerView, loads order data from arguments,
     * updates UI fields, configures step progress view,
     * and submits products to adapter.
     *
     * @param view Fragment root view.
     * @param savedInstanceState Previously saved state.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val order = args.order
        setupOrderRv()

        binding.apply {

            imageCloseOrder.setOnClickListener {
                findNavController().navigateUp()
            }

            tvOrderId.text = "Order #${order.orderId}"

            stepView.setSteps(
                mutableListOf(
                    getString(R.string.status_ordered),
                    getString(R.string.status_confirmed),
                    getString(R.string.status_shipped),
                    getString(R.string.status_delivered)
                )
            )

            val status = getOrderStatus(order.orderStatus)
            val currentOrderState = when(status){
                is OrderStatus.Ordered -> 0
                is OrderStatus.Confirmed -> 1
                is OrderStatus.Shipped -> 2
                is OrderStatus.Delivered -> 3
                is OrderStatus.Canceled -> -1
            }

            if (status is OrderStatus.Canceled) {
                stepView.visibility = View.GONE
                tvCanceledStatus.visibility = View.VISIBLE
            } else {
                stepView.visibility = View.VISIBLE
                tvCanceledStatus.visibility = View.GONE
                stepView.go(currentOrderState, false)
                if (currentOrderState == 3) {
                    stepView.done(true)
                }
            }

            tvFullName.text = order.address.fullName
            tvAddress.text = "${order.address.street} ${order.address.city}"
            tvPhoneNumber.text = order.address.phone
            tvTotalPrice.text =  "$ ${order.totalPrice}"

        }

        // we want to update the product in our list
        billingProductsAdapter.differ.submitList(order.products)

    }

    /**
     * Configures the RecyclerView used for displaying ordered products.
     *
     * Sets:
     * - Adapter
     * - Vertical LinearLayoutManager
     * - Item decoration spacing
     */
    private fun setupOrderRv() {
        binding.rvProducts.apply {
            adapter = billingProductsAdapter
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            addItemDecoration(VerticalItemDecoration())

        }
    }
}