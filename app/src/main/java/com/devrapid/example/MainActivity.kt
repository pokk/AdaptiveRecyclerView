package com.devrapid.example

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.devrapid.adaptiverecyclerview.AdaptiveAdapter
import com.devrapid.adaptiverecyclerview.IVisitable
import com.devrapid.example.model.Animal
import com.devrapid.example.model.Person
import com.devrapid.example.viewtype.MultiTypeFactory
import kotlinx.android.synthetic.main.activity_main.recyclerView

class MainActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val itemList: MutableList<IVisitable<MultiTypeFactory>> = mutableListOf(Person("Google"),
            Person("Facebook"),
            Animal("CEO"),
            Animal("CTO"),
            Animal("CDO"),
            Animal("CAO"),
            Animal("COO"),
            Person("Apple"),
            Animal("AEO"),
            Animal("ATO"),
            Animal("ADO"),
            Person("Airbnb"),
            Person("Jieyi"))

        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = object: AdaptiveAdapter<MultiTypeFactory, IVisitable<MultiTypeFactory>>(itemList) {
            override var typeFactory: MultiTypeFactory = MultiTypeFactory()
        }
    }
}
