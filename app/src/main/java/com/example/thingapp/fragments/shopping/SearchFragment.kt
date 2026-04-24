package com.example.thingapp.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.thingapp.R
import com.example.thingapp.adapters.BestProductsAdapter
import com.example.thingapp.databinding.FragmentSearchBinding
import com.example.thingapp.util.Resource
import com.example.thingapp.viewmodel.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : Fragment(R.layout.fragment_search) {
    private lateinit var binding: FragmentSearchBinding
    private val viewModel by viewModels<SearchViewModel>()
    private val searchAdapter by lazy { BestProductsAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSearchRv()
        setupCategoryClickListeners()

        var searchJob: Job? = null
        binding.edSearch.addTextChangedListener { searchQuery ->
            searchJob?.cancel()
            val query = searchQuery.toString()
            if (query.isEmpty()) {
                showCategories(true)
                searchAdapter.differ.submitList(emptyList())
            } else {
                showCategories(false)
                searchJob = lifecycleScope.launch {
                    delay(500L)
                    viewModel.searchProducts(query)
                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.search.collectLatest {
                    when (it) {
                        is Resource.Loading -> {
                            binding.searchProgressBar.visibility = View.VISIBLE
                        }
                        is Resource.Success -> {
                            binding.searchProgressBar.visibility = View.GONE
                            searchAdapter.differ.submitList(it.data)
                        }
                        is Resource.Error -> {
                            binding.searchProgressBar.visibility = View.GONE
                        }
                        else -> {
                            binding.searchProgressBar.visibility = View.GONE
                        }
                    }
                }
            }
        }

        searchAdapter.onClick = {
            val bundle = Bundle().apply { putParcelable("product", it) }
            findNavController().navigate(R.id.action_searchFragment_to_productDetailsFragment, bundle)
        }

        binding.tvCancel.setOnClickListener {
            binding.edSearch.setText("")
            hideKeyboard()
        }
    }

    private fun showCategories(show: Boolean) {
        binding.categoriesHeader.isVisible = show
        binding.categoriesScrollView.isVisible = show
        binding.rvSearch.isVisible = !show
    }

    private fun setupCategoryClickListeners() {
        binding.cardChair.setOnClickListener { navigateToHomeWithTab(1) }
        binding.cardCupboard.setOnClickListener { navigateToHomeWithTab(2) }
        binding.cardTable.setOnClickListener { navigateToHomeWithTab(3) }
        binding.cardAccessory.setOnClickListener { navigateToHomeWithTab(4) }
        binding.cardFurniture.setOnClickListener { navigateToHomeWithTab(5) }
    }

    private fun navigateToHomeWithTab(tabIndex: Int) {
        val bundle = Bundle().apply {
            putInt("tabIndex", tabIndex)
        }
        findNavController().navigate(R.id.homeFragment, bundle)
    }

    private fun setupSearchRv() {
        binding.rvSearch.apply {
            layoutManager = GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
            adapter = searchAdapter
        }
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }
}