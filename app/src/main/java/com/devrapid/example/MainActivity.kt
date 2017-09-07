package com.devrapid.example

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.devrapid.example.model.Animal
import com.devrapid.example.model.Person
import kotlinx.android.synthetic.main.activity_main.recyclerView

class MainActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val itemList: MutableList<IExpandVisitor> = mutableListOf(Person("Google"),
            Person("Facebook", listOf(Animal("CEO"),
                Animal("CTO"),
                Animal("CDO"),
                Animal("CAO"),
                Animal("COO")), true),
            Person("Apple", listOf(Animal("AEO"),
                Animal("ATO"),
                Animal("ADO"))),
            Person("Airbnb"),
            Person("Jieyi"))

        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
//        recyclerView.layoutManager = WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = ExpandAdapter(itemList)
    }
}
