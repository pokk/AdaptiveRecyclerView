package com.devrapid.adaptiverecyclerview

interface AdapterCollection<VT : ViewTypeFactory, M : IVisitable<VT>> {
    /**
     * Show the all items' detail information because the adapter doesn't allow others to get
     * the list data. Just allow showing the list content.
     *
     * @return a string with the detail of whole list items.
     */
    fun listDescription(): String

    /**
     * Append a list of items to the last of the adapter's data list's by using queue message
     * for avoiding the race condition to break the correct order.
     * If there're the same entities, [androidx.recyclerview.widget.DiffUtil] will filter it.
     *
     * @param list a new list the user wants to append.
     */
    fun append(list: MutableList<M>) = Unit

    /**
     * Add a item to to the last of the adapter's data list by using queue message for avoiding
     * the race condition to break the correct order.
     * If there's the same entities, [androidx.recyclerview.widget.DiffUtil] will filter it.
     *
     * @param item a new item entity the user wants to append.
     */
    fun append(item: M)

    /**
     * Add a list to the assigned position of the adapter's data list by using queue message for
     * avoiding the race condition to break the correct order.
     * If there's the same entities, [androidx.recyclerview.widget.DiffUtil] will filter it.
     *
     * @param position between 0 and last of item size.
     * @param list a new list the user wants to insert.
     */
    fun add(position: Int, list: MutableList<M>)

    /**
     * Add a item to the assigned position of the adapter's data list by using queue message for
     * avoiding the race condition to break the correct order.
     * If there's the same entities, [androidx.recyclerview.widget.DiffUtil] will filter it.
     *
     * @param position between 0 and last of item size.
     * @param item a new item entity the user wants to insert.
     */
    fun add(position: Int, item: M)

    /**
     * Remove a list by range from adapter list.
     *
     * @param range a range of removing a list from adapter.
     */
    fun dropRange(range: IntRange)

    /**
     * Remove a item entity from assigned position.
     *
     * @param index
     */
    fun dropAt(index: Int)

    /**
     * Remove whole items from the adapter list.
     *
     * @param header if true, [headerEntity] will be removed, otherwise, keeps it.
     * @param footer if true, [footerEntity] will be removed, otherwise, keeps it.
     */
    fun clearList(header: Boolean = true, footer: Boolean = true)

    /**
     * Remove whole items then add the [newList] into the adapter list.
     *
     * @param newList MutableList<M>
     */
    fun replaceWholeList(newList: MutableList<M>)

    /**
     * Add header entity(always the top in the list) into the adapter.
     *
     * @param header M
     */
    fun setHeader(header: M)

    /**
     * Add footer entity(always the bottom in the list) into the adapter.
     *
     * @param footer M
     */
    fun setFooter(footer: M)
}
