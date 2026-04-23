package com.example.thingapp.adapters

import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.thingapp.data.FaqItem
import com.example.thingapp.databinding.ItemFaqBinding

/**
 * [RecyclerView.Adapter] that renders a list of [FaqItem]s in the Help / FAQ screen.
 *
 * Each row displays a question badge, the question text, and a chevron icon.
 * Tapping the question row toggles [FaqItem.isExpanded] and reveals or hides
 * the corresponding answer with a 220 ms chevron rotation animation.
 *
 * @constructor Receives the mutable FAQ list directly; no [androidx.recyclerview.widget.DiffUtil]
 *   is needed because the list is static and never updated after creation.
 * @param items The ordered list of [FaqItem]s to display.
 */
class FaqAdapter(private val items: MutableList<FaqItem>) :
    RecyclerView.Adapter<FaqAdapter.FaqViewHolder>() {

    /**
     * Holds references to the views inside a single FAQ row via [ItemFaqBinding].
     *
     * @param binding View binding for `item_faq.xml`.
     */
    inner class FaqViewHolder(private val binding: ItemFaqBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * Binds a [FaqItem] to this view holder.
         *
         * Sets the question and answer texts, syncs the initial expanded state
         * without animation, and attaches a click listener that toggles the state.
         *
         * @param item The [FaqItem] to render.
         */
        fun bind(item: FaqItem) {
            binding.tvQuestion.text = item.question
            binding.tvAnswer.text   = item.answer

            applyExpandedState(item.isExpanded, animate = false)

            binding.layoutQuestion.setOnClickListener {
                item.isExpanded = !item.isExpanded
                applyExpandedState(item.isExpanded, animate = true)
            }
        }

        /**
         * Shows or hides the answer section and rotates the chevron accordingly.
         *
         * When [animate] is `true` the chevron rotates over 220 ms using
         * [ObjectAnimator]; when `false` the rotation is applied instantly
         * (used during [bind] to avoid unwanted animation on first render).
         *
         * @param expanded `true` to expand (show answer), `false` to collapse.
         * @param animate  Whether to animate the chevron rotation.
         */
        private fun applyExpandedState(expanded: Boolean, animate: Boolean) {
            val targetVisibility = if (expanded) View.VISIBLE else View.GONE
            val targetRotation   = if (expanded) 270f else 90f

            binding.tvAnswer.visibility   = targetVisibility
            binding.dividerFaq.visibility = targetVisibility

            if (animate) {
                ObjectAnimator.ofFloat(
                    binding.ivChevron, "rotation",
                    binding.ivChevron.rotation, targetRotation
                ).apply {
                    duration = 220
                    start()
                }
            } else {
                binding.ivChevron.rotation = targetRotation
            }
        }
    }

    /**
     * Inflates `item_faq.xml` and wraps it in a [FaqViewHolder].
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FaqViewHolder {
        val binding = ItemFaqBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FaqViewHolder(binding)
    }

    /**
     * Delegates binding to [FaqViewHolder.bind] for the item at [position].
     */
    override fun onBindViewHolder(holder: FaqViewHolder, position: Int) {
        holder.bind(items[position])
    }

    /** Returns the total number of FAQ entries. */
    override fun getItemCount(): Int = items.size
}
