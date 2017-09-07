package com.devrapid.adaptiverecyclerview

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup

/**
 *
 *
 * @author  jieyi
 * @since   9/6/17
 */
abstract class AdaptiveAdapter<VT: ViewTypeFactory, M: IVisitable<VT>, VH: RecyclerView.ViewHolder>:
    RecyclerView.Adapter<VH>() {
    protected abstract var typeFactory: VT
    protected abstract var dataList: MutableList<M>

    //region Necessary override methods.
    override fun getItemCount(): Int = this.dataList.size

    override fun getItemViewType(position: Int): Int {
        return this.dataList[position].type(this.typeFactory)
    }

    override fun onBindViewHolder(holder: VH, position: Int) =
        (holder as AdaptiveViewHolder<VT, M>).initView(this.dataList[position], position, this)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val itemView: View = View.inflate(parent.context, this.typeFactory.getLayoutResource(viewType), null)

        return this.typeFactory.createViewHolder(viewType, itemView) as VH
    }
    //endregion
}
