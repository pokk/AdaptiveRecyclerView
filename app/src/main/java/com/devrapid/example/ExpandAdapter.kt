package com.devrapid.example

import android.support.v7.util.DiffUtil
import com.devrapid.adaptiverecyclerview.AdaptiveAdapter
import com.devrapid.adaptiverecyclerview.AdaptiveViewHolder
import com.devrapid.example.viewtype.MultiTypeFactory

/**
 * @author  jieyi
 * @since   9/7/17
 */
class ExpandAdapter(override var dataList: MutableList<IExpandVisitor>):
    AdaptiveAdapter<MultiTypeFactory, IExpandVisitor, AdaptiveViewHolder<MultiTypeFactory, IExpandVisitor>>() {
    override var typeFactory: MultiTypeFactory = MultiTypeFactory()
    private val originalParentPosition: MutableList<Int> = MutableList(this.dataList.size, { 0 })

    class ExpandDiffUtil(private var oldList: MutableList<IExpandVisitor>,
                         private var newList: MutableList<IExpandVisitor>): DiffUtil.Callback() {
        override fun getOldListSize(): Int = this.oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            this.oldList[oldItemPosition].hashCode() == this.newList[newItemPosition].hashCode()

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean = true
    }

    fun expand(position: Int, newIndex: Int) {
        this.updateList {
            val subList = this.dataList[newIndex].let {
                this.changeVisibleChildNumber(position, it.childItemList.size)
                it.isExpandable = false
                it.childItemList
            }
            ArrayList(dataList).toMutableList().apply { addAll(newIndex + 1, subList as Collection<IExpandVisitor>) }
        }
    }

    fun collapse(position: Int, newIndex: Int) {
        this.updateList {
            val subList = this.dataList[newIndex].let {
                this.changeVisibleChildNumber(position, 0)
                it.isExpandable = true
                it.childItemList
            }
            ArrayList(dataList).toMutableList().apply { subList(newIndex + 1, newIndex + 1 + subList.size).clear() }
        }
    }

    fun calculateIndex(oldPos: Int): Int = (0..(oldPos - 1)).sumBy { this.originalParentPosition[it] } + oldPos

    fun isCollapsed(position: Int): Boolean = this.dataList[position].isExpandable

    private fun updateList(getNewListBlock: () -> MutableList<IExpandVisitor>) {
        val newList = getNewListBlock()
        val res = DiffUtil.calculateDiff(ExpandDiffUtil(this.dataList, newList), true)
        this.dataList = newList
        res.dispatchUpdatesTo(this)
    }

    private fun changeVisibleChildNumber(index: Int, size: Int) {
        this.originalParentPosition[index] = size
    }
}