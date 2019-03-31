package com.devrapid.example

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.devrapid.example.model.Animal
import com.devrapid.example.model.Person
import kotlinx.android.synthetic.main.activity_main.btn_add
import kotlinx.android.synthetic.main.activity_main.btn_minus
import kotlinx.android.synthetic.main.activity_main.recyclerView

class MainActivity : AppCompatActivity() {
    var a = 1
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

        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val adapter = ExpandAdapter().apply {
            append(itemList)
        }
        adapter.setOnFinishListener {
            //            runBlocking { delay(1000) }
        }
        recyclerView.adapter = adapter

//        adapter.headerEntity = Person("Google @@@@@@@@")
//        adapter.footerEntity = Person("Google !!!!!!!!")

        btn_add.setOnClickListener {
            adapter.append(mutableListOf<IExpandVisitor>(Person("BBBBBBBB ${a++}"),
                                                         Person("BBBBBBBB ${a++}"),
                                                         Person("BBBBBBBB ${a++}"),
                                                         Person("BBBBBBBB ${a++}")))
            adapter.footerEntity = Person("Google @@@@@@@@")
        }
        btn_minus.setOnClickListener {
            adapter.clearList()
        }
    }
}
