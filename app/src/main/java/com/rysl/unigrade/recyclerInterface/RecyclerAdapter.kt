package com.rysl.unigrade.recyclerInterface

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.rysl.unigrade.R
import com.rysl.unigrade.learningTree.Learner


class RecyclerAdapter(val dataset: ArrayList<Learner>, private val itemListener: RecyclerViewClickListener): RecyclerView.Adapter<RecyclerAdapter.ViewLayout>() {

    class ViewLayout(view: View, private val itemListener: RecyclerViewClickListener): RecyclerView.ViewHolder(view), View.OnClickListener, View.OnLongClickListener{
        var cardWidgets = arrayOf<TextView>(view.findViewById(R.id.nameContent),
                view.findViewById(R.id.addButton), view.findViewById(R.id.currentWorkingPercentage),
                view.findViewById(R.id.nameContent), view.findViewById(R.id.typeContent))
        var cv = view.findViewById<CardView>(R.id.card)

        init{
            view.setOnClickListener(this)
            view.setOnLongClickListener(this)
        }

        override fun onClick(v: View?) {
            itemListener.recyclerViewListClicked(this.layoutPosition)
        }

        override fun onLongClick(v: View?): Boolean {
            itemListener.recyclerViewListLongClicked(this.layoutPosition)
            return false
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewLayout {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_layout, parent, false)
        return ViewLayout(view, itemListener)
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    override fun onBindViewHolder(holder: ViewLayout, position: Int) {
        val learner = dataset[position]
        val cardContents = arrayOf("", "", "", "", "")
        when(learner.getPercentage()){
            -2 -> {cardContents[1] = learner.getName()}
            -1 -> {cardContents[0] = learner.getName()}
            else -> {cardContents[3] = "${learner.getPercentage()}%"
                cardContents[2] = "predicted percentage: ${learner.workingPercentage()}%"
            cardContents[4] = when(learner.getType()){
                true -> "Test"
                else -> "Coursework"
            }}
        }
        cardContents.forEachIndexed { index, item ->
            holder.cardWidgets[index].text = item
        }
    }
}