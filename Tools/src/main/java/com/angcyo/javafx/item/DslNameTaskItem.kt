package com.angcyo.javafx.item

import com.angcyo.javafx.bean.NameTaskBean
import com.angcyo.javafx.list.DslListItem
import com.angcyo.javafx.list.DslListItemCell
import com.angcyo.javafx.ui.gridPane
import com.angcyo.javafx.ui.label
import com.angcyo.javafx.ui.resetChildren
import com.angcyo.javafx.ui.setGridIndex
import javafx.geometry.Insets
import javafx.scene.Node
import javafx.scene.layout.GridPane

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/01/08
 */
class DslNameTaskItem : DslListItem() {

    var nameTaskBean: NameTaskBean? = null

    override fun onCreateItem(itemCell: DslListItemCell): Node? {
        return itemCell.graphic ?: gridPane(1, 5) {
            val value = 5.0
            padding = Insets(value, value, value, value)
        }
    }

    override fun onBindItem(itemCell: DslListItemCell, rootNode: Node?) {
        super.onBindItem(itemCell, rootNode)
        (rootNode as? GridPane)?.apply {
            //background = background(Color.AQUA)
            resetChildren(listOf(
                label {
                    text = "账号:\n${nameTaskBean?.username}"
                    setGridIndex(0, 0)
                },
                label {
                    text = "密码:\n${nameTaskBean?.password}"
                    setGridIndex(0, 1)
                },
                label {
                    text = "企业类型:\n${nameTaskBean?.companyTypeName} ${nameTaskBean?.nzCompanyTypeName ?: ""}"
                    setGridIndex(0, 2)
                },
                label {
                    text = "字号:\n${nameTaskBean?.companyWord}"
                    setGridIndex(0, 3)
                },
                label {
                    text = "行业用语:\n${nameTaskBean?.termsWord}"
                    setGridIndex(0, 4)
                }
            ))
        }
    }
}