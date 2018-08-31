package com.devrapid.adaptiverecyclerview

import androidx.recyclerview.widget.DiffUtil

abstract class AdaptiveDiffUtil<VT : ViewTypeFactory, M : IVisitable<VT>> : DiffUtil.Callback() {
    abstract var oldList: MutableList<M>
    abstract var newList: MutableList<M>
}
