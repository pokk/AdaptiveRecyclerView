package com.devrapid.example

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.devrapid.example.model.Animal
import com.devrapid.example.model.Person

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
                Animal("ADO")), true),
            Person("Airbnb"),
            Person("Jieyi"))

        recyclerView.layoutManager =
            androidx.recyclerview.widget.LinearLayoutManager(this,
                                                             androidx.recyclerview.widget.LinearLayoutManager.VERTICAL,
                                                             false)
        recyclerView.adapter = ExpandAdapter(itemList)
    }
}
