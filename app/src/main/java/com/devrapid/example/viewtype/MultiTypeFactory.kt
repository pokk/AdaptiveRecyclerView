package com.devrapid.example.viewtype

import android.support.v7.widget.RecyclerView.ViewHolder
import android.view.View
import com.devrapid.adaptiverecyclerview.ViewTypeFactory
import com.devrapid.example.R
import com.devrapid.example.model.Animal
import com.devrapid.example.model.Person
import com.devrapid.example.viewholder.FirstViewHolder
import com.devrapid.example.viewholder.SecondViewHolder

/**
 *
 *
 * @author  jieyi
 * @since   9/6/17
 */
class MultiTypeFactory: ViewTypeFactory() {
    override var transformMap: MutableMap<Int, Pair<Int, (View) -> ViewHolder>> =
        mutableMapOf(1 to Pair(R.layout.item_first, { itemView -> FirstViewHolder(itemView) }),
            2 to Pair(R.layout.item_second, { itemView -> SecondViewHolder(itemView) }))

    fun type(person: Person): Int = 1

    fun type(animal: Animal): Int = 2
}
