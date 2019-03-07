package com.devrapid.adaptiverecyclerview

internal class DefaultMultiDiffUtil<VT : ViewTypeFactory, M : IVisitable<VT>> : AdaptiveDiffUtil<VT, M>() {
    override var oldList = mutableListOf<M>()
    override var newList = mutableListOf<M>()

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldList[oldItemPosition].hashCode() == newList[newItemPosition].hashCode()

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) = true
}
