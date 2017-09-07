package com.devrapid.example.viewholder

import android.view.View
import com.devrapid.adaptiverecyclerview.AdaptiveViewHolder
import com.devrapid.example.ExpandAdapter
import com.devrapid.example.IExpandVisitor
import com.devrapid.example.model.Person
import com.devrapid.example.viewtype.MultiTypeFactory
import com.devrapid.kotlinknifer.logv
import kotlinx.android.synthetic.main.item_first.view.btn
import kotlinx.android.synthetic.main.item_first.view.tv_title

/**
 *
 *
 * @author  jieyi
 * @since   9/6/17
 */
class FirstViewHolder(view: View): AdaptiveViewHolder<MultiTypeFactory, IExpandVisitor>(
    view) {
    override fun initView(model: IExpandVisitor, position: Int, adapter: Any) {
        adapter as ExpandAdapter
        this.itemView.tv_title.text = (model as Person).name
        this.itemView.btn.setOnClickListener {
            val newPosition = adapter.calculateIndex(position)
            logv(newPosition)
            if (adapter.isCollapsed(newPosition)) {
                adapter.expand(position, newPosition)
            }
            else {
                adapter.collapse(position, newPosition)
            }
        }
    }
}