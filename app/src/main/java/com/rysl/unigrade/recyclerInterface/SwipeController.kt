package com.rysl.unigrade.recyclerInterface

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class SwipeController(recyclerViewClickListener: RecyclerViewClickListener): ItemTouchHelper.Callback() {
    private val clickListener = recyclerViewClickListener

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        return if (viewHolder.layoutPosition != 0 && !clickListener.menuVisible()) {
            makeMovementFlags(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)
        } else 0
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        return false;
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        if (viewHolder.layoutPosition != 0) {
            clickListener.recyclerViewListSwiped(viewHolder.layoutPosition)
        }
    }
}