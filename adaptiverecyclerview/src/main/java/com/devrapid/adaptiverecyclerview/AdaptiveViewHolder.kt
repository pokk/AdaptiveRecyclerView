package com.devrapid.adaptiverecyclerview

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * An abstract view holder.
 *
 * @author  jieyi
 * @since   9/6/17
 */
abstract class AdaptiveViewHolder<in VT : ViewTypeFactory, in M : IVisitable<VT>>(view: View) :
    RecyclerView.ViewHolder(view) {
    protected val mContext: Context = view.context

    /**
     * Set the views' properties.
     *
     * @param model     a data model after input from a list.
     * @param position  the index of a list.
     * @param adapter   parent adapter.
     */
    abstract fun initView(model: M, position: Int, adapter: Any)
}
