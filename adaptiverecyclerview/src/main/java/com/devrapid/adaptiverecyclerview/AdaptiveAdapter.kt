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
    open var diffUtil: AdaptiveDiffUtil<VT, M> = MultiDiffUtil()
    open var useDiffUtilUpdate = true

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

    // OPTIMIZE(jieyi): 2018/12/04 There's no checking bounding.
    open fun appendList(list: MutableList<M>) {
        val startIndex = dataList.size
        val newList = dataList.toMutableList().apply { addAll(startIndex, list) }
        updateList { newList }
    }

    open fun append(item: M) {
        val newList = dataList.toMutableList().apply { add(item) }
        updateList { newList }
    }

    open fun add(position: Int, item: M) {
        var size = dataList.size
        if (headerEntity != null) size--
        if (footerEntity != null) size--

        if (size <= 0) throw IndexOutOfBoundsException()

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

    open fun dropList(startIndex: Int, endIndex: Int) {
        when {
            startIndex < 0 || endIndex >= dataList.size -> throw IndexOutOfBoundsException("The range is over than list.")
            startIndex > endIndex -> throw IndexOutOfBoundsException("startIndex index must be less than endIndex index.")
        }
        val newList = dataList.toMutableList()

        repeat(endIndex - startIndex + 1) { newList.removeAt(startIndex) }
        updateList { newList }
    }

    open fun dropAt(index: Int) = dropList(index, index)

    open fun clearList(header: Boolean = true, footer: Boolean = true): Boolean {
        var from = 0
        var to = dataList.size - 1

        if (dataList.size <= 0)
            return false
        if (!header && headerEntity != null)
            from++
        if (!footer && headerEntity != null)
            to--

        dropList(from, to)

        return true
    }

    open fun replaceWholeList(newList: MutableList<M>) {
        updateList { newList }
    }

    private fun updateList(getNewListBlock: () -> MutableList<M>) {
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
