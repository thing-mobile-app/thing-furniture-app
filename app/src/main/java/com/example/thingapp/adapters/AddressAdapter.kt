package com.example.thingapp.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.thingapp.R
import com.example.thingapp.data.Address
import com.example.thingapp.databinding.AddressRvItemBinding

/**
 * Adapter for managing and displaying delivery addresses in a horizontal list.
 * Features a selection mechanism to highlight the active shipping destination.
 */
class AddressAdapter : RecyclerView.Adapter<AddressAdapter.AddressViewHolder>() {

    // Track the index of the currently selected address
    var selectedAddress = -1

    inner class AddressViewHolder(val binding: AddressRvItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(address: Address, isSelected: Boolean) {
            binding.apply {
                tvAddressTitle.text = address.addressTitle

                // Professional visual feedback for selection state
                if (isSelected) {
                    containerAddress.setBackgroundResource(R.color.g_blue) // Or your primary blue
                    tvAddressTitle.setTextColor(Color.WHITE)
                } else {
                    containerAddress.setBackgroundResource(R.color.g_white)
                    tvAddressTitle.setTextColor(Color.BLACK)
                }
            }
        }
    }

    private val diffUtil = object : DiffUtil.ItemCallback<Address>() {
        override fun areItemsTheSame(oldItem: Address, newItem: Address): Boolean =
            oldItem.addressTitle == newItem.addressTitle

        override fun areContentsTheSame(oldItem: Address, newItem: Address): Boolean =
            oldItem == newItem
    }

    val differ = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        return AddressViewHolder(
            AddressRvItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        val address = differ.currentList[position]
        holder.bind(address, selectedAddress == position)

        holder.itemView.setOnClickListener {
            if (selectedAddress >= 0) notifyItemChanged(selectedAddress)
            selectedAddress = holder.adapterPosition
            notifyItemChanged(selectedAddress)
            onClick?.invoke(address)
        }
    }

    override fun getItemCount(): Int = differ.currentList.size

    var onClick: ((Address) -> Unit)? = null
}