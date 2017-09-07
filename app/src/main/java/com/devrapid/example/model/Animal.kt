package com.devrapid.example.model

import com.devrapid.example.IExpandVisitor
import com.devrapid.example.viewtype.MultiTypeFactory

/**
 *
 *
 * @author  jieyi
 * @since   9/6/17
 */
data class Animal(var name: String,
                  override var childItemList: List<*> = emptyList<IExpandVisitor>(),
                  override var isExpandable: Boolean = false): IExpandVisitor {
    override fun type(typeFactory: MultiTypeFactory): Int = typeFactory.type(this)
}