package com.example.thingapp.util

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * A custom [RecyclerView.ItemDecoration] that adds vertical spacing between items.
 *
 * This decoration applies a fixed offset to the bottom of each item in the [RecyclerView]
 * to ensure consistent vertical separation.
 *
 * @property amount The vertical space (in pixels) to be added to the bottom of each item.
 * Default value is 30.
 */
class VerticalItemDecoration(private val amount: Int = 30): RecyclerView.ItemDecoration() {
    /**
     * Configures the offsets for the given item.
     *
     * @param outRect Rect to receive the output. Each field represents the number
     * of pixels that the item view should be inset by.
     * @param view The child view to decorate.
     * @param parent The RecyclerView these items belong to.
     * @param state The current state of RecyclerView.
     */
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.bottom = amount
    }
}