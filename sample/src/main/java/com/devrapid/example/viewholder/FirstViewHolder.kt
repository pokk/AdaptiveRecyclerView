package com.devrapid.example.viewholder

import android.view.View
import android.widget.Button
import android.widget.TextView
import com.devrapid.adaptiverecyclerview.AdaptiveViewHolder
import com.devrapid.example.ExpandAdapter
import com.devrapid.example.R
import com.devrapid.example.model.Person
import com.devrapid.example.viewtype.MultiTypeFactory

/**
 * @author jieyi
 * @since 9/6/17
 */
class FirstViewHolder(view: View) : AdaptiveViewHolder<MultiTypeFactory, Person>(view) {
    override fun initView(model: Person, position: Int, adapter: Any) {
        adapter as ExpandAdapter
        itemView.findViewById<TextView>(R.id.tv_title).text = model.name
        itemView.findViewById<Button>(R.id.btn).setOnClickListener {
            val newPosition = adapter.calculateIndex(position)
            if (adapter.isCollapsed(newPosition)) {
                adapter.expand(position, newPosition)
            } else {
                adapter.collapse(position, newPosition)
            }
        }
    }
}
