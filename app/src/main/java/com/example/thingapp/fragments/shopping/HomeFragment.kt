package com.example.thingapp.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.thingapp.R
import com.example.thingapp.adapters.HomeViewpagerAdapter
import com.example.thingapp.databinding.FragmentHomeBinding
import com.example.thingapp.fragments.categories.AccessoryFragment
import com.example.thingapp.fragments.categories.ChairFragment
import com.example.thingapp.fragments.categories.CupboardFragment
import com.example.thingapp.fragments.categories.FurnitureFragment
import com.example.thingapp.fragments.categories.MainCategoryFragment
import com.example.thingapp.fragments.categories.TableFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class HomeFragment : Fragment(R.layout.fragment_home) {
    private lateinit var binding : FragmentHomeBinding

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

        // list of category fragments
        val categoriesFragments = arrayListOf<Fragment>(
            MainCategoryFragment(),
            ChairFragment(),
            CupboardFragment(),
            TableFragment(),
            AccessoryFragment(),
            FurnitureFragment()
        )

        // viewpager allows us to swipe between fragments and adapter tells the viewpager what fragments to use
        val viewpager2Adapter = HomeViewpagerAdapter(categoriesFragments, childFragmentManager, lifecycle)
        // attach the adapter to the viewpager ( now viewpager will use data from the adapter)
        binding.viewPagerHome.adapter = viewpager2Adapter

        // it connects the tabLayout to the viewpager
        // when you swipe , tabLayout will change to the corresponding fragment
        // when you click on tab , viewpager will change to the corresponding fragment
        TabLayoutMediator(binding.tabLayout, binding.viewPagerHome){
            // reference to our tab and position
            tab, position ->
            when(position) {
                0 -> tab.text = "Main"
                1 -> tab.text = "Chair"
                2 -> tab.text = "Cupboard"
                3 -> tab.text = "Table"
                4 -> tab.text = "Accessory"
                5 -> tab.text = "Furniture"
            }
            // attach the tabLayout to the viewpager
        }.attach()

    }
}