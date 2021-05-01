package com.devrapid.example.viewholder

import android.view.View
import android.widget.TextView
import com.devrapid.adaptiverecyclerview.AdaptiveViewHolder
import com.devrapid.example.ExpandAdapter
import com.devrapid.example.R
import com.devrapid.example.model.Animal
import com.devrapid.example.viewtype.MultiTypeFactory

/**
 * @author  jieyi
 * @since   9/6/17
 */
class SecondViewHolder(view: View): AdaptiveViewHolder<MultiTypeFactory, Animal>(view) {
    override fun initView(model: Animal, position: Int, adapter: Any) {
        adapter as ExpandAdapter
        itemView.findViewById<TextView>(R.id.tv_subtitle).text = model.name
    }
}