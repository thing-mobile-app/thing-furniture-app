package com.example.thingapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.thingapp.databinding.SizeRvItemBinding

class SizesAdapter : RecyclerView.Adapter<SizesAdapter.SizesAdapterViewHolder>() {

    private var selectedPosition = -1

    inner class SizesAdapterViewHolder(private val binding : SizeRvItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(size : String, position: Int){
            // ULTIMATE mapping to ensure short letters (S, M, L, XL)
            val displaySize = when(size.uppercase().trim()) {
                "S", "SMALL" -> "S"
                "M", "MEDIUM" -> "M"
                "L", "LARGE" -> "L"
                "XL", "EXTRA LARGE", "BIG", "EXTRA-LARGE" -> "XL"
                else -> size.take(1).uppercase() // Fallback to first letter
            }
            binding.tvSize.text = displaySize
            
            if(position == selectedPosition){
                binding.imageShadow.visibility = View.VISIBLE
            } else{
                binding.imageShadow.visibility = View.INVISIBLE
            }
        }
    }

    private val diffCallBack = object : DiffUtil.ItemCallback<String>(){
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean = oldItem == newItem
        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean = oldItem == newItem
    }

    val differ = AsyncListDiffer(this, diffCallBack)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SizesAdapterViewHolder {
        return SizesAdapterViewHolder(
            SizeRvItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(
        holder: SizesAdapterViewHolder,
        position: Int
    ) {
        val size = differ.currentList[position]
        holder.bind(size, position)

        holder.itemView.setOnClickListener {
            if(selectedPosition >= 0) notifyItemChanged(selectedPosition)
            selectedPosition = holder.adapterPosition
            notifyItemChanged(selectedPosition)
            onItemClick?.invoke(size)
        }
    }

    override fun getItemCount(): Int = differ.currentList.size

    var onItemClick:((String) -> Unit)? = null
}