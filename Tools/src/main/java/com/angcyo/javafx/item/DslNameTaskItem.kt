package com.angcyo.javafx.item

import com.angcyo.javafx.base.ex.find
import com.angcyo.javafx.bean.NameTaskBean
import com.angcyo.javafx.list.DslListItem
import com.angcyo.javafx.list.DslListItemCell
import com.angcyo.javafx.list.itemIndex
import com.angcyo.javafx.ui.*
import com.angcyo.javafx.web.Task
import com.angcyo.library.ex.toTime
import javafx.geometry.HPos
import javafx.geometry.Insets
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.scene.paint.Color
import javafx.scene.text.TextFlow

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/01/08
 */
class DslNameTaskItem : DslListItem() {

    var nameTaskBean: NameTaskBean? = null
    val paddingValue = 5.0

    var deleteAction: (NameTaskBean) -> Unit = {}

    override fun onCreateItem(itemCell: DslListItemCell): Node? {
        return itemCell.graphic ?: gridPane(2, 6, columnConfig = { index, columnConstraints ->
            if (index >= 5) {
                columnConstraints.halignment = HPos.RIGHT
            }
        }) {
            padding = Insets(paddingValue, paddingValue, paddingValue, paddingValue)
            resetChildren(listOf(
                //row 0
                label {
                    id = "taskTip"
                    setGridConstraints(0, 0, 1, 3)
                    padding = Insets(paddingValue, paddingValue, paddingValue, 0.0)
                    textFill = Color.DEEPSKYBLUE
                },
                hBox {
                    id = "controlPane"
                    setGridIndex(0, 5)
                },
                //row 1
                textFlow {
                    id = "usernameFlow"
                    setGridIndex(1, 0)
                    //text = "账号:\n${nameTaskBean?.username}"
                },
                textFlow {
                    id = "passwordFlow"
                    setGridIndex(1, 1)
                    //text = "密码:\n${nameTaskBean?.password}"
                },
                textFlow {
                    id = "companyFlow"
                    setGridIndex(1, 2)
                    //text = "企业类型:\n${nameTaskBean?.companyTypeName} ${nameTaskBean?.nzCompanyTypeName ?: ""}"
                },
                textFlow {
                    id = "companyWordFlow"
                    setGridIndex(1, 3)
                    //text = "字号:\n${nameTaskBean?.companyWord}"
                },
                textFlow {
                    id = "termsWordFlow"
                    setGridIndex(1, 4)
                    //text = "行业用语:\n${nameTaskBean?.termsWord}"
                },
                textFlow {
                    id = "stateFlow"
                    setGridIndex(1, 5)
                }
            ))
        }
    }

    override fun onBindItem(itemCell: DslListItemCell, rootNode: Node?) {
        super.onBindItem(itemCell, rootNode)
        (rootNode as? GridPane)?.apply {
            //background = background(Color.AQUA)
            find<Label>("taskTip")?.text = "任务:${itemIndex() + 1}  创建时间:${nameTaskBean?.createTime?.toTime() ?: ""}"
            find<HBox>("controlPane")?.resetChildren(
                listOf(
                    checkBox {
                        text = "激活"
                        isSelected = nameTaskBean?.enable == true
                        setOnAction {
                            nameTaskBean?.enable = isSelected
                            Task.saveNameTask()
                            updateItem(itemCell)
                        }
                    },
                    button {
                        text = "删除"
                        setOnAction {
                            if (nameTaskBean != null && alertConfirm("删除后, 不可恢复!")) {
                                deleteAction(nameTaskBean!!)
                            }
                        }
                    }
                )
            )
            find<TextFlow>("usernameFlow")?.resetChildren(
                listOf(
                    text("帐号:\n", "#666666".toColor()),
                    text(nameTaskBean?.username ?: "")
                )
            )
            find<TextFlow>("passwordFlow")?.resetChildren(
                listOf(
                    text("密码:\n", "#666666".toColor()),
                    text(nameTaskBean?.password ?: "")
                )
            )
            find<TextFlow>("companyFlow")?.resetChildren(
                listOf(
                    text("企业类型:\n", "#666666".toColor()),
                    text("${nameTaskBean?.companyTypeName} ${nameTaskBean?.nzCompanyTypeName ?: ""}")
                )
            )
            find<TextFlow>("companyWordFlow")?.resetChildren(
                listOf(
                    text("字号:\n", "#666666".toColor()),
                    text(nameTaskBean?.companyWord ?: "")
                )
            )
            find<TextFlow>("termsWordFlow")?.resetChildren(
                listOf(
                    text("行业用语:\n", "#666666".toColor()),
                    text(nameTaskBean?.termsWord ?: "")
                )
            )
            find<TextFlow>("stateFlow")?.resetChildren(
                listOf(
                    text("任务状态:\n", Color.DEEPSKYBLUE),
                    text(
                        when (nameTaskBean?.taskState) {
                            NameTaskBean.STATE_NORMAL -> if (nameTaskBean?.enable == true) "就绪" else "未激活"
                            NameTaskBean.STATE_RUNNING -> "运行中"
                            NameTaskBean.STATE_FINISH -> "已完成"
                            else -> "--"
                        }
                    )
                )
            )
        }
    }
}