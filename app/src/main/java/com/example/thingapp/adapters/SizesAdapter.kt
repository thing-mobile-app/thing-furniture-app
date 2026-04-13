package com.example.thingapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.thingapp.databinding.SizeRvItemBinding

// Adapter for displaying selectable size options in a RecyclerView
// Same concept as ColorsAdapter.kt but for sizes we use string instead of Int
class SizesAdapter : RecyclerView.Adapter<SizesAdapter.SizesAdapterViewHolder>() {

    private var selectedPosition = -1


    inner class SizesAdapterViewHolder(private val binding : SizeRvItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(size : String, position: Int){
            binding.tvSize.text = size
            // Size is selected
            if(position == selectedPosition){
                binding.apply {
                    imageShadow.visibility = View.VISIBLE
                }
            }
            // Size is not selected
            else{
                binding.apply {
                    imageShadow.visibility = View.INVISIBLE
                }
            }
        }

    }

    private val diffCallBack = object : DiffUtil.ItemCallback<String>(){ // Sizes are String
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, diffCallBack)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SizesAdapterViewHolder {
        return SizesAdapterViewHolder(
            SizeRvItemBinding.inflate(LayoutInflater.from(parent.context))
        )
    }

    override fun onBindViewHolder(
        holder: SizesAdapterViewHolder,
        position: Int
    ) {
        val size = differ.currentList[position]
        holder.bind(size, position)

        holder.itemView.setOnClickListener {
            if(selectedPosition >= 0){
                notifyItemChanged(selectedPosition) // Used to unselect the selected item
            }
            // We will select the new item by these two lines
            selectedPosition = holder.adapterPosition
            notifyItemChanged(position)
            onItemClick?.invoke(size)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    var onItemClick:((String) -> Unit)? = null

}