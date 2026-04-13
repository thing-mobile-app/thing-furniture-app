package com.example.thingapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.thingapp.databinding.ViewpagerImageItemBinding

// Adapter used to display product images inside ViewPager2
class ViewPager2Images : RecyclerView.Adapter<ViewPager2Images.ViewPager2ImagesViewHolder>() {

    // ViewHolder: represents each item view
    inner class ViewPager2ImagesViewHolder(val binding : ViewpagerImageItemBinding) : RecyclerView.ViewHolder(binding.root){

        // Binds image data to the view using Glide
        fun bind(imagePath : String){
            Glide.with(itemView).load(imagePath).into(binding.imageProductDetails)
        }
    }

    // DiffUtil for efficient list updates
    private val diffCallBack = object : DiffUtil.ItemCallback<String>(){
        // Checks if two items represent the same object
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        // Checks if the content of items is the same
        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

    }

    // Handles list updates asynchronously
    val differ = AsyncListDiffer(this, diffCallBack)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewPager2ImagesViewHolder {
        return ViewPager2ImagesViewHolder(
            ViewpagerImageItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        )
    }

    // Binds data to each item based on position
    override fun onBindViewHolder(
        holder: ViewPager2ImagesViewHolder,
        position: Int
    ) {
       val image = differ.currentList[position] // get image at position
        holder.bind(image)
    }

    // Returns total number of items
    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}