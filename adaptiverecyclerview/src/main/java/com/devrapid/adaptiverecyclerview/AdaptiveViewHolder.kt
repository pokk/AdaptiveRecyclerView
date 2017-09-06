package com.devrapid.adaptiverecyclerview

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * An abstract view holder.
 *
 * @author  jieyi
 * @since   9/6/17
 */
abstract class AdaptiveViewHolder<VT: ViewTypeFactory, M: IVisitable<VT>>(view: View): RecyclerView.ViewHolder(view) {
    protected val mContext: Context = view.context

    /**
     * Set the views' properties.
     *
     * @param model     a data model after input from a list.
     * @param position  the index of a list.
     * @param adaptiveAdapter   parent adaptiveAdapter.
     */
    abstract fun initView(model: M, position: Int, adaptiveAdapter: AdaptiveAdapter<VT, M>)
}