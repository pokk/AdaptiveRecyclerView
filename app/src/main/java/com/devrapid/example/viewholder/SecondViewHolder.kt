package com.devrapid.example.viewholder

import android.view.View
import com.devrapid.adaptiverecyclerview.AdaptiveAdapter
import com.devrapid.adaptiverecyclerview.AdaptiveViewHolder
import com.devrapid.example.model.Animal
import com.devrapid.example.viewtype.MultiTypeFactory

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