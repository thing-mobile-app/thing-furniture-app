package com.example.thingapp.fragments.categories

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.thingapp.R
import com.example.thingapp.adapters.BestDealsAdapter
import com.example.thingapp.adapters.BestProductsAdapter
import com.example.thingapp.adapters.SpecialProductsAdapter
import com.example.thingapp.databinding.FragmentMainCategoryBinding
import com.example.thingapp.util.Resource
import com.example.thingapp.viewmodel.MainCategoryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainCategoryFragment : Fragment(R.layout.fragment_main_category) {
    private lateinit var binding: FragmentMainCategoryBinding

    // define the adapters
    private val specialProductsAdapter by lazy { SpecialProductsAdapter() }
    private val bestDealsAdapter by lazy { BestDealsAdapter() }
    private val bestProductsAdapter by lazy { BestProductsAdapter() }

    private val viewModel by viewModels<MainCategoryViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentMainCategoryBinding.bind(view)

        // set up the RecyclerViews
        setupSpecialProductsRv()
        setupBestDealsRv()
        setupBestProductsRv()

        // listen to the data.
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {


                launch {
                    viewModel.specialProducts.collectLatest { resource ->
                        when (resource) {
                            is Resource.Loading -> showLoading()
                            is Resource.Success -> {
                                specialProductsAdapter.differ.submitList(resource.data)
                                hideLoading()
                            }
                            is Resource.Error -> {
                                hideLoading()
                                Log.e("MainCategoryFragment", resource.message.toString())
                                Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                            }
                            else -> Unit
                        }
                    }
                }


                launch {
                    viewModel.bestDealsProducts.collectLatest { resource ->
                        when (resource) {
                            is Resource.Loading -> showLoading()
                            is Resource.Success -> {
                                bestDealsAdapter.differ.submitList(resource.data)
                                hideLoading()
                            }
                            is Resource.Error -> {
                                hideLoading()
                                Log.e("MainCategoryFragment", resource.message.toString())
                                Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                            }
                            else -> Unit
                        }
                    }
                }


                launch {
                    viewModel.bestProducts.collectLatest { resource ->
                        when (resource) {
                            is Resource.Loading -> binding.bestProductsProgressBar.visibility = View.VISIBLE
                            is Resource.Success -> {
                                bestProductsAdapter.differ.submitList(resource.data)
                                binding.bestProductsProgressBar.visibility = View.GONE
                            }
                            is Resource.Error -> {
                                binding.bestProductsProgressBar.visibility = View.GONE
                                Log.e("MainCategoryFragment", resource.message.toString())
                                Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                            }
                            else -> Unit
                        }
                    }
                }
            }
        }
        binding.nestedScrollMainCategory.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener{ v,_,scrollY,_,_ ->
            if (v.getChildAt(0).bottom <=v.height + scrollY){
                viewModel.fetchBestProducts()
            }
        })
    }

    private fun setupSpecialProductsRv() {
        binding.rvSpecialProducts.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = specialProductsAdapter
        }
    }

    private fun setupBestDealsRv() {

        binding.rvBestDealsProducts.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = bestDealsAdapter
        }
    }

    private fun setupBestProductsRv() {

        // Here we use GridLayoutManager to display 2 products side by side.
        binding.rvBestProducts.apply {
            layoutManager = GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
            adapter = bestProductsAdapter
        }
    }

    private fun showLoading() {
        binding.mainCategoryProgressBar.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        binding.mainCategoryProgressBar.visibility = View.GONE
    }
}
