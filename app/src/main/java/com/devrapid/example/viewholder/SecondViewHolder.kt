package com.devrapid.example.viewholder

import android.view.View
import com.devrapid.example.model.Animal
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
class SecondViewHolder(view: View): AdaptiveViewHolder<MultiTypeFactory, Animal>(view) {
    override fun initView(model: Animal, position: Int, adaptiveAdapter: AdaptiveAdapter<MultiTypeFactory, Animal>) {
    }
}