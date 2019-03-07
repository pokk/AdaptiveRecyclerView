package com.devrapid.example

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.DOWN
import androidx.recyclerview.widget.ItemTouchHelper.LEFT
import androidx.recyclerview.widget.ItemTouchHelper.RIGHT
import androidx.recyclerview.widget.ItemTouchHelper.UP
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.devrapid.example.model.Person
import kotlinx.android.synthetic.main.activity_deletable.recyclerView

class DeletableActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deletable)

        val itemList: MutableList<IExpandVisitor> = mutableListOf(Person("Google"),
                                                                  Person("Facebook"),
                                                                  Person("Apple"),
                                                                  Person("Amazon"),
                                                                  Person("HTC"),
                                                                  Person("Banana"),
                                                                  Person("Grape"),
                                                                  Person("Airbnb"),
                                                                  Person("Jieyi"))
        val adapter = ExpandAdapter().apply { add(0, itemList) }

        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(UP or DOWN, LEFT or RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ) = true

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.layoutPosition
                adapter.dropRange(position..position)
            }
        }).attachToRecyclerView(recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = adapter
    }
}
