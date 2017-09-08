package com.devrapid.example.viewholder

import android.view.View
import com.devrapid.adaptiverecyclerview.AdaptiveViewHolder
import com.devrapid.example.ExpandAdapter
import com.devrapid.example.model.Animal
import com.devrapid.example.viewtype.MultiTypeFactory
import kotlinx.android.synthetic.main.item_second.view.tv_subtitle

/**
 * @author  jieyi
 * @since   9/6/17
 */
class SecondViewHolder(view: View): AdaptiveViewHolder<MultiTypeFactory, Animal>(view) {
    override fun initView(model: Animal, position: Int, adapter: Any) {
        adapter as ExpandAdapter
        this.itemView.tv_subtitle.text = model.name
    }
}