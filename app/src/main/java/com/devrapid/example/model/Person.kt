package com.devrapid.example.model

import com.devrapid.adaptiverecyclerview.IVisitable
import com.devrapid.example.viewtype.MultiTypeFactory


/**
 *
 *
 * @author  jieyi
 * @since   9/6/17
 */
data class Person(var name: String): IVisitable<MultiTypeFactory> {
    override fun type(typeFactory: MultiTypeFactory): Int = typeFactory.type(this)
}