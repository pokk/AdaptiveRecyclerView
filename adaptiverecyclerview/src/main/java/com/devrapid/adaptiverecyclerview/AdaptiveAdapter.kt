package com.devrapid.adaptiverecyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.devrapid.adaptiverecyclerview.MessageType.MESSAGE_ADD_FOOTER
import com.devrapid.adaptiverecyclerview.MessageType.MESSAGE_ADD_HEADER
import com.devrapid.adaptiverecyclerview.MessageType.MESSAGE_ADD_LIST
import com.devrapid.adaptiverecyclerview.MessageType.MESSAGE_ADD_SINGLE
import com.devrapid.adaptiverecyclerview.MessageType.MESSAGE_APPEND_LIST
import com.devrapid.adaptiverecyclerview.MessageType.MESSAGE_APPEND_SINGLE
import com.devrapid.adaptiverecyclerview.MessageType.MESSAGE_DROP_ALL
import com.devrapid.adaptiverecyclerview.MessageType.MESSAGE_DROP_RANGE
import com.devrapid.adaptiverecyclerview.MessageType.MESSAGE_DROP_SINGLE
import com.devrapid.adaptiverecyclerview.MessageType.MESSAGE_REPLACE_ALL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.ArrayDeque

/**
 * An adaptive [RecyclerView] which accepts multiple type layout.
 *
 * @author  jieyi
 * @since   9/6/17
 */
abstract class AdaptiveAdapter<VT : ViewTypeFactory, M : IVisitable<VT>, VH : RecyclerView.ViewHolder> :
    RecyclerView.Adapter<VH>(), AdapterCollection<VT, M> {
    //region Overridable variables.
    protected abstract var typeFactory: VT
    protected abstract var dataList: MutableList<M>
    open var diffUtil: AdaptiveDiffUtil<VT, M> = DefaultMultiDiffUtil()
    open var useDiffUtilUpdate = true
    /** The real data list count. */
    val dataItemCount: Int
        get() {
            var size = dataList.size
            if (headerEntity != null) size--
            if (footerEntity != null) size--

            return size
        }
    //endregion
    //region Private variables.
    /** Header item. */
    private var headerEntity: M? = null
    /** Footer item. */
    private var footerEntity: M? = null
    /** The queue for adding all tasks by inserting, removing, modifying the list. */
    private val queue = ArrayDeque<Message<M>>()
    // Don't forget unbinding this listener.
    private var onFinishListener: DiffProcessCallback? = null
    //endregion

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

    /**
     * Show the all items' detail information because the adapter doesn't allow others to get
     * the list data. Just allow showing the list content.
     *
     * @return a string with the detail of whole list items.
     */
    override fun listDescription() = dataList.joinToString("\n") { it.toString() }

    /**
     * Append a list of items to the last of the adapter's data list's by using queue message
     * for avoiding the race condition to break the correct order.
     * If there're the same entities, [DiffUtil] will filter it.
     *
     * @param list a new list the user wants to append.
     */
    override fun append(list: MutableList<M>) {
        queue.add(Message<M>().also {
            it.type = MESSAGE_APPEND_LIST
            it.newList = list
        })
        runUpdateTask()
    }

    /**
     * Add a item to to the last of the adapter's data list by using queue message for avoiding
     * the race condition to break the correct order.
     * If there's the same entities, [DiffUtil] will filter it.
     *
     * @param item a new item entity the user wants to append.
     */
    override fun append(item: M) {
        queue.add(Message<M>().also {
            it.type = MESSAGE_APPEND_SINGLE
            it.newItem = item
        })
        runUpdateTask()
    }

    /**
     * Add a list to the assigned position of the adapter's data list by using queue message for
     * avoiding the race condition to break the correct order.
     * If there's the same entities, [DiffUtil] will filter it.
     *
     * @param position between 0 and last of item size.
     * @param list a new list the user wants to insert.
     */
    override fun add(position: Int, list: MutableList<M>) {
        queue.add(Message<M>().also {
            it.type = MESSAGE_ADD_LIST
            it.position = position
            it.newList = list
        })
        runUpdateTask()
    }

    /**
     * Add a item to the assigned position of the adapter's data list by using queue message for
     * avoiding the race condition to break the correct order.
     * If there's the same entities, [DiffUtil] will filter it.
     *
     * @param position between 0 and last of item size.
     * @param item a new item entity the user wants to insert.
     */
    override fun add(position: Int, item: M) {
        queue.add(Message<M>().also {
            it.type = MESSAGE_ADD_SINGLE
            it.position = position
            it.newItem = item
        })
        runUpdateTask()
    }

    /**
     * Remove a list by range from adapter list.
     *
     * @param range a range of removing a list from adapter.
     */
    override fun dropRange(range: IntRange) {
        queue.add(Message<M>().also {
            it.type = MESSAGE_DROP_RANGE
            it.range = range
        })
        runUpdateTask()
    }

    /**
     * Remove a item entity from assigned position.
     *
     * @param index
     */
    override fun dropAt(index: Int) {
        queue.add(Message<M>().also {
            it.type = MESSAGE_DROP_SINGLE
            it.position = index
        })
        runUpdateTask()
    }

    /**
     * Remove whole items from the adapter list.
     *
     * @param header if true, [headerEntity] will be removed, otherwise, keeps it.
     * @param footer if true, [footerEntity] will be removed, otherwise, keeps it.
     */
    override fun clearList(header: Boolean, footer: Boolean) {
        queue.add(Message<M>().also {
            it.type = MESSAGE_DROP_ALL
            it.header = header
            it.footer = footer
        })
        runUpdateTask()
    }

    /**
     * Remove whole items then add the [newList] into the adapter list.
     *
     * @param newList MutableList<M>
     */
    override fun replaceWholeList(newList: MutableList<M>) {
        queue.add(Message<M>().also {
            it.type = MESSAGE_REPLACE_ALL
            it.newList = newList
        })
        runUpdateTask()
    }

    override fun setHeader(header: M) {
        if (header == headerEntity) return
        queue.add(Message<M>().also {
            it.type = MESSAGE_ADD_HEADER
            it.newItem = header
            it.oldItem = headerEntity
        })
        runUpdateTask()
    }

    override fun setFooter(footer: M) {
        if (footer == footerEntity) return
        queue.add(Message<M>().also {
            it.type = MESSAGE_ADD_FOOTER
            it.newItem = footer
            it.oldItem = footerEntity
        })
        runUpdateTask()
    }

    fun setOnFinishListener(callback: (() -> Unit)?) {
        if (callback == null) {
            onFinishListener = null
            return
        }

        onFinishListener = object : DiffProcessCallback {
            override fun onProcessFinish() {
                callback()
            }
        }
    }

    //region Inner operations
    protected open fun _append(list: MutableList<M>): MutableList<M> {
        var startIndex = dataList.size
        if (footerEntity != null)
            startIndex--
        // [toMutableList()] will create a new [ArrayList].
        return dataList.toMutableList().apply { addAll(startIndex, list) }
    }

    protected open fun _append(item: M) = dataList.toMutableList().apply {
        if (footerEntity != null) add(dataList.size - 1, item) else add(item)
    }

    protected open fun _add(position: Int, list: MutableList<M>) = dataList.toMutableList().apply {
        addAll(position + (if (headerEntity == null) 0 else 1), list)
    }

    protected open fun _add(position: Int, item: M): MutableList<M> {
        if (dataItemCount <= 0) throw IndexOutOfBoundsException()

        return dataList.toMutableList().apply {
            add(position + (if (headerEntity == null) 0 else 1), item)
        }
    }

    protected open fun _dropRange(range: IntRange): MutableList<M> {
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

        return newList
    }

    protected open fun _dropAt(index: Int) = _dropRange(index..index)

    protected open fun _clearList(header: Boolean = true, footer: Boolean = true): MutableList<M> = runBlocking {
        withContext(Dispatchers.Main) {
            if (header) headerEntity = null
            if (footer) footerEntity = null
        }

        mutableListOf<M>().apply {
            headerEntity?.let(::add)
            footerEntity?.let(::add)
        }
    }

    /**
     * It will replace whole list to a new list. Replacing is not including
     * the [headerEntity] and [footerEntity].
     *
     * @param newList
     */
    protected open fun _replaceWholeList(newList: MutableList<M>) = newList.toMutableList().apply {
        headerEntity?.let { add(0, it) }
        footerEntity?.let { add(newList.size + (if (headerEntity == null) 0 else 1), it) }
    }

    private fun _setHeader(value: M?, headerEntity: M?): MutableList<M>? {
        value?.let {
            if (headerEntity == null) {
                dataList.add(0, it)
                notifyItemInserted(0)
            }
            else {
                dataList.removeAt(0)
                dataList.add(0, it)
                GlobalScope.launch(Dispatchers.Main) { notifyItemChanged(0) }
            }
        } ?: run {
            if (headerEntity != null && dataList.size > 0) {
                dataList.removeAt(0)
                GlobalScope.launch(Dispatchers.Main) { notifyItemRemoved(0) }
            }
        }
        this.headerEntity = value
        return null
    }

    private fun _setFooter(value: M?, footerEntity: M?): MutableList<M>? {
        value?.let {
            if (footerEntity == null) {
                dataList.add(dataList.size, it)
                GlobalScope.launch(Dispatchers.Main) { notifyItemInserted(dataList.size) }
            }
            else {
                dataList.removeAt(dataList.size - 1)
                dataList.add(dataList.size, it)
                GlobalScope.launch(Dispatchers.Main) { notifyItemChanged(dataList.size - 1) }
            }
        } ?: run {
            if (footerEntity != null && dataList.size - 1 > 0) {
                dataList.removeAt(dataList.size - 1)
                GlobalScope.launch(Dispatchers.Main) { notifyItemRemoved(dataList.size) }
            }
        }
        this.footerEntity = value
        return null
    }
    //endregion

    //region Real doing update task
    private fun runUpdateTask() {
        if (queue.size > 1) return
        update(queue.peek())
    }

    private fun update(message: Message<M>) {
        GlobalScope.launch {
            val list = extractUpdateList(message)
            val res = if (list != null)
                DiffUtil.calculateDiff(diffUtil.apply {
                    oldList = dataList
                    newList = list
                })
            else null

            withContext(Dispatchers.Main) {
                if (list != null) {
                    dataList = list
                    res?.dispatchUpdatesTo(this@AdaptiveAdapter)
                }
                queue.remove()
                // Check the queue is still having message.
                if (queue.size > 0)
                    update(queue.peek())
                GlobalScope.launch(Dispatchers.Main) { onFinishListener?.onProcessFinish() }
            }
        }
    }

    private fun extractUpdateList(message: Message<M>): MutableList<M>? = when (message.type) {
        MESSAGE_APPEND_LIST -> _append(message.newList)
        MESSAGE_APPEND_SINGLE -> _append(requireNotNull(message.newItem))
        MESSAGE_ADD_LIST -> _add(message.position, message.newList)
        MESSAGE_ADD_SINGLE -> _add(message.position, requireNotNull(message.newItem))
        MESSAGE_DROP_RANGE -> _dropRange(message.range)
        MESSAGE_DROP_SINGLE -> _dropAt(message.position)
        MESSAGE_DROP_ALL -> _clearList(message.header, message.footer)
        MESSAGE_REPLACE_ALL -> _replaceWholeList(message.newList)
        MESSAGE_ADD_HEADER -> _setHeader(message.newItem, message.oldItem)
        MESSAGE_ADD_FOOTER -> _setFooter(message.newItem, message.oldItem)
        else -> null
    }
    //endregion
}
