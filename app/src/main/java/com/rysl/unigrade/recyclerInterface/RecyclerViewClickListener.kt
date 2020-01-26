package com.rysl.unigrade.recyclerInterface

interface RecyclerViewClickListener {
    fun recyclerViewListClicked(position: Int)

    fun recyclerViewListSwiped(position: Int)

    fun recyclerViewListLongClicked(position: Int)

    fun menuVisible(): Boolean
}