package com.devrapid.adaptiverecyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
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
    RecyclerView.Adapter<VH>() {
    protected abstract var typeFactory: VT
    protected abstract var dataList: MutableList<M>
    //region Header and Footer
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
    //endregion
    open var diffUtil: AdaptiveDiffUtil<VT, M> = DefaultMultiDiffUtil()
    open var useDiffUtilUpdate = true
    val dataItemCount: Int
        get() {
            var size = dataList.size
            if (headerEntity != null) size--
            if (footerEntity != null) size--

            return size
        }
    private val queue = ArrayDeque<Message<M>>()
    // Don't forget unbinding this listener.
    private var onFinishListener: DiffProcessCallback? = null

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

    open fun append(list: MutableList<M>) {
        queue.add(Message<M>().also {
            it.type = MESSAGE_APPEND_LIST
            it.newList = list
        })
        runUpdateTask()
    }

    open fun append(item: M) {
        queue.add(Message<M>().also {
            it.type = MESSAGE_APPEND_SINGLE
            it.newItem = item
        })
        runUpdateTask()
    }

    open fun add(position: Int, list: MutableList<M>) {
        queue.add(Message<M>().also {
            it.type = MESSAGE_ADD_LIST
            it.position = position
            it.newList = list
        })
        runUpdateTask()
    }

    open fun add(position: Int, item: M) {
        queue.add(Message<M>().also {
            it.type = MESSAGE_ADD_SINGLE
            it.newItem = item
        })
        runUpdateTask()
    }

    open fun dropRange(range: IntRange) {
        queue.add(Message<M>().also {
            it.type = MESSAGE_DROP_RANGE
            it.range = range
        })
        runUpdateTask()
    }

    open fun dropAt(index: Int) {
        queue.add(Message<M>().also {
            it.type = MESSAGE_DROP_SINGLE
            it.position = index
        })
        runUpdateTask()
    }

    open fun clearList(header: Boolean = true, footer: Boolean = true) {
        queue.add(Message<M>().also {
            it.type = MESSAGE_DROP_ALL
            it.header = header
            it.footer = footer
        })
        runUpdateTask()
    }

    open fun replaceWholeList(newList: MutableList<M>) {
        queue.add(Message<M>().also {
            it.type = MESSAGE_REPLACE_ALL
            it.newList = newList
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
    //endregion

    //region Real doing update task
    private fun runUpdateTask() {
        if (queue.size > 1) return
        update(queue.peek())
    }

    private fun update(message: Message<M>) {
        GlobalScope.launch {
            val list = extractUpdateList(message)
            val res = DiffUtil.calculateDiff(diffUtil.apply {
                oldList = dataList
                newList = list
            })

            withContext(Dispatchers.Main) {
                dataList = list
                res.dispatchUpdatesTo(this@AdaptiveAdapter)
                queue.remove()
                // Check the queue is still having message.
                if (queue.size > 0)
                    update(queue.peek())
                GlobalScope.launch(Dispatchers.Main) { onFinishListener?.onProcessFinish() }
            }
        }
    }

    private fun extractUpdateList(message: Message<M>) = when (message.type) {
        MESSAGE_APPEND_LIST -> _append(message.newList)
        MESSAGE_APPEND_SINGLE -> _append(requireNotNull(message.newItem))
        MESSAGE_ADD_LIST -> _add(message.position, message.newList)
        MESSAGE_ADD_SINGLE -> _add(message.position, requireNotNull(message.newItem))
        MESSAGE_DROP_RANGE -> _dropRange(message.range)
        MESSAGE_DROP_SINGLE -> _dropAt(message.position)
        MESSAGE_DROP_ALL -> _clearList(message.header, message.footer)
        MESSAGE_REPLACE_ALL -> _replaceWholeList(message.newList)
        else -> mutableListOf()
    }
    //endregion
}
