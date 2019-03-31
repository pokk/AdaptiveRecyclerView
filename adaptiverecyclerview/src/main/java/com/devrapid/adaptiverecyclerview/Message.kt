package com.devrapid.adaptiverecyclerview

internal class Message<M> {
    var type = -1
    var position = -1
    var range = -1..-1
    var newList = mutableListOf<M>()
    var newItem: M? = null
    var oldItem: M? = null
    var header = true
    var footer = true
}
