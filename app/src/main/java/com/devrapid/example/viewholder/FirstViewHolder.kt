package com.devrapid.example.viewholder

import android.view.View
import com.devrapid.adaptiverecyclerview.AdaptiveAdapter
import com.devrapid.adaptiverecyclerview.AdaptiveViewHolder
import com.devrapid.example.model.Person
import com.devrapid.example.viewtype.MultiTypeFactory

/**
 *
 *
 * @author  jieyi
 * @since   9/6/17
 */
class FirstViewHolder(view: View): AdaptiveViewHolder<MultiTypeFactory, Person>(view) {
    override fun initView(model: Person, position: Int, adaptiveAdapter: AdaptiveAdapter<MultiTypeFactory, Person>) {
    }
}