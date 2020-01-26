package com.rysl.unigrade.recyclerInterface

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rysl.unigrade.advancedDataTypes.Stack_L
import com.rysl.unigrade.learningTree.Learner
import com.rysl.unigrade.learningTree.Subject

class RecyclerViewInterface(context: Context, private val recyclerView: RecyclerView,
                            learners: ArrayList<Learner>,
                            recyclerViewClickListener: RecyclerViewClickListener)
                            : View(context) {

    private var manager: RecyclerView.LayoutManager
    private var stack = Stack_L()
    private val addButton = Subject(-2, "+")
    private val dataset = ArrayList<Learner>()

    init{
        dataset.add(addButton)
        dataset.addAll(learners)
        manager = LinearLayoutManager(context)
        val adapter = RecyclerAdapter(dataset, recyclerViewClickListener)
        recyclerView.layoutManager = manager
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.adapter = adapter
        val swipeController = SwipeController(recyclerViewClickListener)
        val itemTouchHelper = ItemTouchHelper(swipeController)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    fun refreshView(learners: ArrayList<Learner>){
        stack.push(dataset)
        dataset.clear()
        dataset.add(addButton)
        dataset.addAll(learners)
        recyclerView.adapter!!.notifyDataSetChanged()
    }

    fun addLearner(learners: ArrayList<Learner>){
        val position = learners.size
        dataset.add(learners.last())
        recyclerView.adapter!!.notifyItemInserted(position)
    }

    fun updateLearner(learner: Learner, position: Int){
        dataset[position] = learner
        recyclerView.adapter!!.notifyItemChanged(position)
    }

    fun deleteLearner(position: Int){
        dataset.removeAt(position)
        recyclerView.adapter!!.notifyItemRemoved(position)
    }
}