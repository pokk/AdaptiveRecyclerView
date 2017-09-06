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
abstract class AdaptiveAdapter<VT: ViewTypeFactory, M: IVisitable<VT>>(private var dataList: MutableList<M>):
    RecyclerView.Adapter<AdaptiveViewHolder<VT, IVisitable<VT>, AdaptiveAdapter<VT, IVisitable<VT>>>>() {
    protected abstract var typeFactory: VT

    //region Necessary override methods.
    override fun getItemCount(): Int = this.dataList.size

    override fun getItemViewType(position: Int): Int = this.dataList[position].type(this.typeFactory)

    override fun onBindViewHolder(holder: AdaptiveViewHolder<VT, IVisitable<VT>, AdaptiveAdapter<VT, IVisitable<VT>>>,
                                  position: Int) =
        holder.initView(this.dataList[position], position, this as AdaptiveAdapter<VT, IVisitable<VT>>)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
        AdaptiveViewHolder<VT, IVisitable<VT>, AdaptiveAdapter<VT, IVisitable<VT>>> {
        val itemView: View = View.inflate(parent.context, this.typeFactory.getLayoutResource(viewType), null)

        return this.typeFactory.createViewHolder(viewType, itemView) as
            AdaptiveViewHolder<VT, IVisitable<VT>, AdaptiveAdapter<VT, IVisitable<VT>>>
    }
    //endregion
}
