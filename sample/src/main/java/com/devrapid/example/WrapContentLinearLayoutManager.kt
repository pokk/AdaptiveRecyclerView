package com.devrapid.example

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log

/**
 * @author  jieyi
 * @since   9/7/17
 */
class WrapContentLinearLayoutManager(context: Context, orientation: Int, reverseLayout: Boolean):
    LinearLayoutManager(context, orientation, reverseLayout) {
    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        try {
            super.onLayoutChildren(recycler, state)
        }
        catch (e: IndexOutOfBoundsException) {
            Log.e("probe", "meet a IOOBE in RecyclerView")
        }
    }
}