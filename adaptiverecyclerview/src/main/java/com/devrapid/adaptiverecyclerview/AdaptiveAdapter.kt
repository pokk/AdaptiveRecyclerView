package com.devrapid.adaptiverecyclerview

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup

/**
 * An adaptive [RecyclerView] which accepts multiple type layout.
 *
 * @author  jieyi
 * @since   9/6/17
 */
abstract class AdaptiveAdapter<VT: ViewTypeFactory, M: IVisitable<VT>, VH: RecyclerView.ViewHolder>:
    RecyclerView.Adapter<VH>() {
    var headerEntity: M? = null
        set(value) {
            value?.let {
                field = it
                dataList.add(0, it)
                notifyItemInserted(0)
            } ?: run {
                dataList.removeAt(0)
                notifyItemRemoved(0)
            }
        }
    var footerEntity: M? = null
        set(value) {
            value?.let {
                field = it
                dataList.add(dataList.size, it)
                notifyItemChanged(dataList.size)
            } ?: run {
                dataList.removeAt(dataList.size)
                notifyItemChanged(dataList.size)
            }
        }
    protected abstract var typeFactory: VT
    protected abstract var dataList: MutableList<M>

    //region Necessary override methods.
    override fun getItemCount(): Int = this.dataList.size

    override fun getItemViewType(position: Int): Int = this.dataList[position].type(this.typeFactory)

    override fun onBindViewHolder(holder: VH, position: Int) =
        (holder as AdaptiveViewHolder<VT, M>).initView(this.dataList[position], position, this)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val itemView: View = View.inflate(parent.context, this.typeFactory.getLayoutResource(viewType), null)

        return this.typeFactory.createViewHolder(viewType, itemView) as VH
    }
    //endregion
}
