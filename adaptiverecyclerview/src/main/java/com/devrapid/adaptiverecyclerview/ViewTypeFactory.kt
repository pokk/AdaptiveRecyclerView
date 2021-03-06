package com.devrapid.adaptiverecyclerview

import android.view.View
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder

/**
 * A base interface of the multi-view type.
 *
 * @author jieyi
 * @since 9/6/17
 */
abstract class ViewTypeFactory {
    /** For obtaining the layout resource id and creating a new [ViewHolder]. */
    abstract var transformMap: MutableMap<Int, Pair<Int, (View) -> ViewHolder>>

    /**
     * Creating a view holder.
     *
     * @param type      a res ID of layout.
     * @param itemView  a view after inflating.
     * @return [RecyclerView.ViewHolder] of recycler view holder.
     */
    fun createViewHolder(type: Int, itemView: View): RecyclerView.ViewHolder =
        this.transformMap[type]?.second?.invoke(itemView) ?: error("You don't set the viewholder function.")

    /**
     * Get layout resource by inputting type.
     */
    @LayoutRes
    fun getLayoutResource(type: Int): Int = transformMap[type]?.first ?: error("You didn't set this view type.")

    protected fun typeToInt(objectName: String): Int = objectName.sumBy(Char::toInt)
}
