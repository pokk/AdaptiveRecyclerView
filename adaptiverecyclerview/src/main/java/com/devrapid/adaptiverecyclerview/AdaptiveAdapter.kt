package com.devrapid.adaptiverecyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

/**
 * An adaptive [RecyclerView] which accepts multiple type layout.
 *
 * @author  jieyi
 * @since   9/6/17
 */
abstract class AdaptiveAdapter<VT : ViewTypeFactory, M : IVisitable<VT>, VH : RecyclerView.ViewHolder> :
    RecyclerView.Adapter<VH>() {
    var headerEntity: M? = null
        set(value) {
            if (field == value) return  // If the same, we don't do operations as the following below.
            value?.let {
                if (field == null) {
                    dataList.add(0, it)
                    notifyItemInserted(0)
                }
                else {
                    dataList.removeAt(0)
                    dataList.add(0, it)
                    notifyItemChanged(0)
                }
            } ?: run {
                if (field != null) {
                    dataList.removeAt(0)
                    notifyItemRemoved(0)
                }
            }
            field = value
        }
    var footerEntity: M? = null
        set(value) {
            if (field == value) return  // If the same, we don't do operations as the following below.
            value?.let {
                if (field == null) {
                    dataList.add(dataList.size, it)
                    notifyItemInserted(dataList.size)
                }
                else {
                    dataList.removeAt(dataList.size - 1)
                    dataList.add(dataList.size, it)
                    notifyItemChanged(dataList.size - 1)
                }
            } ?: run {
                if (field != null) {
                    dataList.removeAt(dataList.size - 1)
                    notifyItemRemoved(dataList.size)
                }
            }
            field = value
        }
    open var diffUtil: AdaptiveDiffUtil<VT, M> = MultiDiffUtil()
    open var useDiffUtilUpdate = true
    val dataItemCount: Int
        get() {
            var size = dataList.size
            if (headerEntity != null) size--
            if (footerEntity != null) size--

            return size
        }

    protected abstract var typeFactory: VT
    protected abstract var dataList: MutableList<M>

    inner class MultiDiffUtil : AdaptiveDiffUtil<VT, M>() {
        override var oldList = mutableListOf<M>()
        override var newList = mutableListOf<M>()

        override fun getOldListSize() = oldList.size

        override fun getNewListSize() = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            oldList[oldItemPosition].hashCode() == newList[newItemPosition].hashCode()

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) = true
    }

    //region Necessary override methods.
    override fun getItemCount() = dataList.size

    override fun getItemViewType(position: Int) = dataList[position].type(typeFactory)

    override fun onBindViewHolder(holder: VH, position: Int) =
        (holder as AdaptiveViewHolder<VT, M>).initView(dataList[position], position, this)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val itemView: View =
            LayoutInflater.from(parent.context).inflate(typeFactory.getLayoutResource(viewType), parent, false)

        return typeFactory.createViewHolder(viewType, itemView) as VH
    }
    //endregion

    fun listDescription() = dataList.joinToString("\n") { it.toString() }

    open fun appendList(list: MutableList<M>) {
        var startIndex = dataList.size
        if (footerEntity != null)
            startIndex--
        // [toMutableList()] will create a new [ArrayList].
        val newList = dataList.toMutableList().apply { addAll(startIndex, list) }
        updateList { newList }
    }

    open fun append(item: M) {
        val newList = dataList.toMutableList().apply {
            if (footerEntity != null) add(dataList.size - 1, item) else add(item)
        }
        updateList { newList }
    }

    open fun add(position: Int, item: M) {
        if (dataItemCount <= 0) throw IndexOutOfBoundsException()

        val newList = dataList.toMutableList().apply {
            add(position + (if (headerEntity == null) 0 else 1), item)
        }
        updateList { newList }
    }

    open fun add(position: Int, list: MutableList<M>) {
        val newList = dataList.toMutableList().apply {
            addAll(position + (if (headerEntity == null) 0 else 1), list)
        }
        updateList { newList }
    }

    open fun dropRange(range: IntRange) {
        var start = range.start

        when {
            start < 0 || range.last > dataItemCount - 1 ->
                throw IndexOutOfBoundsException("The range is over than list. ListSize: ${dataItemCount - 1}, Range:$start..${range.last}")
            start > range.last ->
                throw IndexOutOfBoundsException("startIndex index must be less than endIndex index. Start: $start End: ${range.last}")
        }
        val newList = dataList.toMutableList()

        // Count the range.
        if (headerEntity != null) start++
        repeat(range.count()) { newList.removeAt(start) }
        updateList { newList }
    }

    open fun dropAt(index: Int) {
        dropRange(index..index)
    }

    open fun clearList(header: Boolean = true, footer: Boolean = true): Boolean {
        if (header) headerEntity = null
        if (footer) footerEntity = null

        dropRange(0..(dataItemCount - 1))

        return true
    }

    /**
     * It will replace whole list to a new list. Replacing is not including
     * the [headerEntity] and [footerEntity].
     *
     * @param newList
     */
    open fun replaceWholeList(newList: MutableList<M>) {
        val withHeaderAndFooterList = newList.apply {
            headerEntity?.let { newList.add(0, it) }
            footerEntity?.let { newList.add(newList.size, it) }
        }
        updateList { withHeaderAndFooterList }
    }

    open fun updateList(getNewListBlock: () -> MutableList<M>) {
        val newList = getNewListBlock()
        val res = DiffUtil.calculateDiff(diffUtil.apply {
            oldList = dataList
            this.newList = newList
        })

        dataList = newList
        if (useDiffUtilUpdate)
            res.dispatchUpdatesTo(this)
        else
            notifyDataSetChanged()
    }
}
