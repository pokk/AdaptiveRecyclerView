package com.devrapid.example

import com.devrapid.adaptiverecyclerview.IVisitable
import com.devrapid.example.viewtype.MultiTypeFactory

/**
 * @author  jieyi
 * @since   9/7/17
 */
interface IExpandVisitor: IVisitable<MultiTypeFactory> {
    var childItemList: List<IExpandVisitor>
    var isExpandable: Boolean
}