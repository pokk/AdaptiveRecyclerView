package com.devrapid.example.viewholder

import android.view.View
import com.devrapid.example.model.Person
import com.devrapid.example.viewtype.MultiTypeFactory
import no1.taiwan.devrapid.com.adaptiverecyclerview.AdaptiveAdapter
import no1.taiwan.devrapid.com.adaptiverecyclerview.AdaptiveViewHolder

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