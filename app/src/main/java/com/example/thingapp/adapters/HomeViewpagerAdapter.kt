package com.example.thingapp.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

// FragmentManager manages fragment operations : for example, if user swipe chairs to table, it will remove the chairs fragment and add the table fragment
// Lifecycle manages the lifecycle of the fragment : on viewed fragment is active, near fragment is ready and the far fragment is destroyed
class HomeViewpagerAdapter(private val fragments : List<Fragment>, fm: FragmentManager, lifeCycle : Lifecycle) : FragmentStateAdapter(fm, lifeCycle) {


    // Returns the fragment at this position from the list
    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

    // Returns fragment count
    override fun getItemCount(): Int {
        return fragments.size
    }
}