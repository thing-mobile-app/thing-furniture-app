package com.example.thingapp.fragments.shopping

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.thingapp.R
import com.example.thingapp.activities.LoginRegisterActivity
import com.example.thingapp.databinding.FragmentProfileBinding
import com.example.thingapp.BuildConfig
import com.example.thingapp.util.Resource
import com.example.thingapp.util.showBottomNavigationView
import com.example.thingapp.viewmodel.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private val viewModel by viewModels<ProfileViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onLogoutClick()

        binding.constraintProfile.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_userAccountFragment)
        }

        binding.linearAllOrders.setOnClickListener {
            val action = ProfileFragmentDirections.actionProfileFragmentToAllOrdersFragment(activeOnly = false)
            findNavController().navigate(action)
        }

        binding.linearTrackOrder.setOnClickListener {
            val action = ProfileFragmentDirections.actionProfileFragmentToAllOrdersFragment(activeOnly = true)
            findNavController().navigate(action)
        }

        binding.linearBilling.setOnClickListener {
            val action = ProfileFragmentDirections.actionProfileFragmentToBillingFragment(
                emptyArray(),
                0f,
                false
            )
            findNavController().navigate(action)
        }

        binding.linearLanguage.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_languageFragment)
        }

        binding.tvVersion.text = "${getString(R.string.version)} ${BuildConfig.VERSION_NAME}"

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.user.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.progressbarSettings.visibility = View.VISIBLE
                    }

                    is Resource.Success -> {
                        binding.progressbarSettings.visibility = View.GONE
                        Glide.with(requireView()).load(it.data!!.imagePath)
                            .error(ColorDrawable(Color.BLACK)).into(binding.imageUser)
                        binding.tvUserName.text = "${it.data.firstName} ${it.data.lastName}"
                    }

                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        binding.progressbarSettings.visibility = View.GONE
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun onLogoutClick() {
        binding.linearLogOut.setOnClickListener {
            viewModel.logout()
            val intent = Intent(requireContext(), LoginRegisterActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            requireActivity().finish()
        }
    }

    override fun onResume() {
        super.onResume()
        showBottomNavigationView()
    }
}