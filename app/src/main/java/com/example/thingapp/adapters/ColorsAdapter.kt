package com.example.thingapp.adapters

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.thingapp.databinding.ColorRvItemBinding

// Adapter for displaying selectable color items in a RecyclerView
class ColorsAdapter : RecyclerView.Adapter<ColorsAdapter.ColorsAdapterViewHolder>() {

    // Keeps track of the position
    private var selectedPosition = -1


    // ViewHolder for each color item
    inner class ColorsAdapterViewHolder(private val binding : ColorRvItemBinding) : RecyclerView.ViewHolder(binding.root){

        // Binds color data to the view and handles selection UI
        fun bind(color : Int, position: Int){

            val imageDrawable = ColorDrawable(color)
            binding.imageColor.setImageDrawable(imageDrawable)

            // Color is selected (show selection indicators)
            if(position == selectedPosition){
                binding.apply {
                    imageShadow.visibility = View.VISIBLE
                    imagePicked.visibility = View.VISIBLE
                }
            }
            // Color is not selected (hide selection indicators)
            else{
                binding.apply {
                    imageShadow.visibility = View.INVISIBLE
                    imagePicked.visibility = View.INVISIBLE
                }
            }
        }

    }


    // DiffUtil for efficient list updates --- > same concept
    private val diffCallBack = object : DiffUtil.ItemCallback<Int>(){
        override fun areItemsTheSame(oldItem: Int, newItem: Int): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Int, newItem: Int): Boolean {
            return oldItem == newItem
        }

    }

    // Handles async list diffing
    val differ = AsyncListDiffer(this, diffCallBack)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ColorsAdapterViewHolder {
        return ColorsAdapterViewHolder(
            ColorRvItemBinding.inflate(LayoutInflater.from(parent.context))
        )
    }

    override fun onBindViewHolder(
        holder: ColorsAdapterViewHolder,
        position: Int
    ) {
        val color = differ.currentList[position]
        holder.bind(color, position)

        holder.itemView.setOnClickListener {
            if(selectedPosition >= 0){
                notifyItemChanged(selectedPosition) // Used to unselect the selected item
            }
            // We will select the new item by these two lines
            selectedPosition = holder.adapterPosition
            notifyItemChanged(position)

            // Trigger click listener callback
            onItemClick?.invoke(color)
        }
    }

    // Returns total item count
    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    // Lambda for item click listener
    var onItemClick:((Int) -> Unit)? = null


}