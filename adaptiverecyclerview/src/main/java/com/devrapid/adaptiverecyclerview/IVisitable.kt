package com.devrapid.adaptiverecyclerview

/**
 * An interface for visitors.
 *
 * @author jieyi
 * @since 9/6/17
 */
interface IVisitable<in VT : ViewTypeFactory> {
    fun type(typeFactory: VT): Int
}
