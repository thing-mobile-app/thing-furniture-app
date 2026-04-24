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
import com.example.thingapp.adapters.HomeViewpagerAdapter
import com.example.thingapp.databinding.FragmentHomeBinding
import com.example.thingapp.fragments.categories.AccessoryFragment
import com.example.thingapp.fragments.categories.ChairFragment
import com.example.thingapp.fragments.categories.CupboardFragment
import com.example.thingapp.fragments.categories.FurnitureFragment
import com.example.thingapp.fragments.categories.MainCategoryFragment
import com.example.thingapp.fragments.categories.TableFragment
import com.example.thingapp.util.Resource
import com.example.thingapp.viewmodel.SearchViewModel
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {
    private lateinit var binding : FragmentHomeBinding
    private val searchViewModel by viewModels<SearchViewModel>()
    private val searchAdapter by lazy { BestProductsAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding = FragmentHomeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSearchRv()
        
        val categoriesFragments = arrayListOf<Fragment>(
            MainCategoryFragment(),
            ChairFragment(),
            CupboardFragment(),
            TableFragment(),
            AccessoryFragment(),
            FurnitureFragment()
        )

        val viewpager2Adapter = HomeViewpagerAdapter(categoriesFragments, childFragmentManager, lifecycle)
        binding.viewPagerHome.adapter = viewpager2Adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPagerHome){
            tab, position ->
            when(position) {
                0 -> tab.text = getString(R.string.main)
                1 -> tab.text = getString(R.string.chair)
                2 -> tab.text = getString(R.string.cupboard)
                3 -> tab.text = getString(R.string.table)
                4 -> tab.text = getString(R.string.accessory)
                5 -> tab.text = getString(R.string.furniture)
            }
        }.attach()

        handleArguments()

        var searchJob: Job? = null
        binding.edSearchHome.addTextChangedListener { searchQuery ->
            searchJob?.cancel()
            val query = searchQuery.toString()
            if (query.isEmpty()) {
                showSearch(false)
            } else {
                showSearch(true)
                searchJob = lifecycleScope.launch {
                    delay(500L)
                    searchViewModel.searchProducts(query)
                }
            }
        }

        binding.ivCloseSearch.setOnClickListener {
            binding.edSearchHome.setText("")
            hideKeyboard()
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                searchViewModel.search.collectLatest {
                    when (it) {
                        is Resource.Loading -> {
                            binding.searchProgressBarHome.visibility = View.VISIBLE
                        }
                        is Resource.Success -> {
                            binding.searchProgressBarHome.visibility = View.GONE
                            searchAdapter.differ.submitList(it.data)
                        }
                        is Resource.Error -> {
                            binding.searchProgressBarHome.visibility = View.GONE
                        }
                        else -> {
                            binding.searchProgressBarHome.visibility = View.GONE
                        }
                    }
                }
            }
        }

        searchAdapter.onClick = {
            val bundle = Bundle().apply { putParcelable("product", it) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment, bundle)
        }
    }

    private fun showSearch(show: Boolean) {
        binding.tabLayout.isVisible = !show
        binding.viewPagerHome.isVisible = !show
        binding.rvSearchHome.isVisible = show
        binding.ivCloseSearch.isVisible = show
    }

    private fun handleArguments() {
        val tabIndex = arguments?.getInt("tabIndex", -1) ?: -1
        if (tabIndex != -1) {
            binding.viewPagerHome.post {
                binding.viewPagerHome.setCurrentItem(tabIndex, false)
            }
            arguments?.remove("tabIndex")
        }
    }

    private fun setupSearchRv() {
        binding.rvSearchHome.apply {
            layoutManager = GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
            adapter = searchAdapter
        }
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }
}