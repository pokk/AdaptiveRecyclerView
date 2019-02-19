package com.devrapid.example.viewholder

import android.view.View
import com.devrapid.adaptiverecyclerview.AdaptiveAdapter
import com.devrapid.adaptiverecyclerview.AdaptiveViewHolder
import com.devrapid.example.ExpandAdapter
import com.devrapid.example.model.Person
import com.devrapid.example.viewtype.MultiTypeFactory
import kotlinx.android.synthetic.main.item_first.view.btn
import kotlinx.android.synthetic.main.item_first.view.tv_title

/**
 * @author  jieyi
 * @since   9/6/17
 */
class FirstViewHolder(view: View): AdaptiveViewHolder<MultiTypeFactory, Person>(view) {
    override fun initView(model: Person, position: Int, adapter: AdaptiveAdapter<*, *, *>) {
        adapter as ExpandAdapter
        this.itemView.tv_title.text = model.name
        this.itemView.btn.setOnClickListener {
            val newPosition = adapter.calculateIndex(position)
            if (adapter.isCollapsed(newPosition)) {
                adapter.expand(position, newPosition)
            }
            else {
                adapter.collapse(position, newPosition)
            }
        }
    }
}
