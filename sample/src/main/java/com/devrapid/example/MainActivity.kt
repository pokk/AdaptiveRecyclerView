package com.devrapid.example

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.devrapid.example.model.Animal
import com.devrapid.example.model.Person

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val itemList: MutableList<IExpandVisitor> = mutableListOf(
            Person("Google"),
            Person(
                "Facebook",
                listOf(
                    Animal("CEO"),
                    Animal("CTO"),
                    Animal("CDO"),
                    Animal("CAO"),
                    Animal("COO")
                ),
                true
            ),
            Person(
                "Apple",
                listOf(
                    Animal("AEO"),
                    Animal("ATO"),
                    Animal("ADO")
                ),
                true
            ),
            Person("Airbnb"),
            Person("Jieyi")
        )

        findViewById<RecyclerView>(R.id.recyclerView).apply {
            layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
            adapter = ExpandAdapter(itemList)
        }
    }
}
