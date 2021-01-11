package com.angcyo.javafx.item

import com.angcyo.javafx.list.DslListItem
import com.angcyo.javafx.list.DslListItemCell
import com.angcyo.javafx.ui.*
import com.angcyo.library.ex.des
import com.angcyo.library.ex.or
import com.angcyo.selenium.bean.ActionBean
import com.angcyo.selenium.bean.TaskBean
import javafx.scene.Node
import javafx.scene.control.TitledPane
import javafx.scene.layout.Pane

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/01/07
 */
class DslTaskDebugItem : DslListItem() {

    var taskBean: TaskBean? = null

    var clickAction: (ActionBean) -> Unit = {

    }

    var clickTaskAction: (TaskBean) -> Unit = {

    }

    var doubleAction: (ActionBean) -> Unit = {

    }

    override fun onCreateItem(itemCell: DslListItemCell): Node {
        return itemCell.graphic ?: titledPane {
            flowPane {
                listOf()
            }
        }
    }

    override fun onBindItem(itemCell: DslListItemCell, rootNode: Node?) {
        super.onBindItem(itemCell, rootNode)

        (rootNode as? TitledPane)?.apply {
            text = "${taskBean?.title.or()}${taskBean?.des.des()} 共(${taskBean?.actionList?.size ?: 0})步"
            graphic = button {
                text = "load"
                taskBean?.let { taskBean ->
                    setOnMouseClicked {
                        clickTaskAction(taskBean)
                    }
                }
            }

            //内容
            (content as? Pane)?.children?.reset {
                taskBean?.actionList?.forEachIndexed { index, actionBean ->
                    add(button {
                        text = "${index + 1}: ${actionBean.title.or()}${actionBean.des.des()}"
                        setOnMouseClicked {
                            clickAction(actionBean)
                        }
                        setOnMouseDoubleClicked {
                            doubleAction(actionBean)
                        }
                    })
                }
            }
        }
    }
}