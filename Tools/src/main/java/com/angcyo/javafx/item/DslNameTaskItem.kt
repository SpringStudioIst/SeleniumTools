package com.angcyo.javafx.item

import com.angcyo.javafx.bean.NameTaskBean
import com.angcyo.javafx.list.DslListItem
import com.angcyo.javafx.list.DslListItemCell
import com.angcyo.javafx.list.itemIndex
import com.angcyo.javafx.ui.*
import com.angcyo.library.ex.toTime
import javafx.geometry.Insets
import javafx.scene.Node
import javafx.scene.layout.GridPane
import javafx.scene.paint.Color

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/01/08
 */
class DslNameTaskItem : DslListItem() {

    var nameTaskBean: NameTaskBean? = null
    val paddingValue = 5.0

    override fun onCreateItem(itemCell: DslListItemCell): Node? {
        return itemCell.graphic ?: gridPane(2, 6) {
            padding = Insets(paddingValue, paddingValue, paddingValue, paddingValue)
        }
    }

    override fun onBindItem(itemCell: DslListItemCell, rootNode: Node?) {
        super.onBindItem(itemCell, rootNode)
        (rootNode as? GridPane)?.apply {
            //background = background(Color.AQUA)
            resetChildren(listOf(
                //row 0
                label {
                    setGridConstraints(0, 0, 1, 3)
                    padding = Insets(paddingValue, paddingValue, paddingValue, 0.0)
                    text = "任务:${itemIndex() + 1}  时间:${nameTaskBean?.createTime?.toTime() ?: ""}"
                    textFill = Color.DEEPSKYBLUE
                },
                /*button {
                    text = "开始"
                    setGridIndex(0, 5)
                    setOnAction {
                        L.i("this...")
                    }
                },*/

                //row 1
                textFlow {
                    setGridIndex(1, 0)
                    //text = "账号:\n${nameTaskBean?.username}"
                    resetChildren(
                        listOf(
                            text("帐号:\n", "#666666".toColor()),
                            text(nameTaskBean?.username ?: "")
                        )
                    )
                },
                textFlow {
                    setGridIndex(1, 1)
                    //text = "密码:\n${nameTaskBean?.password}"
                    resetChildren(
                        listOf(
                            text("密码:\n", "#666666".toColor()),
                            text(nameTaskBean?.password ?: "")
                        )
                    )
                },
                textFlow {
                    setGridIndex(1, 2)
                    //text = "企业类型:\n${nameTaskBean?.companyTypeName} ${nameTaskBean?.nzCompanyTypeName ?: ""}"
                    resetChildren(
                        listOf(
                            text("企业类型:\n", "#666666".toColor()),
                            text("${nameTaskBean?.companyTypeName} ${nameTaskBean?.nzCompanyTypeName ?: ""}")
                        )
                    )
                },
                textFlow {
                    setGridIndex(1, 3)
                    //text = "字号:\n${nameTaskBean?.companyWord}"
                    resetChildren(
                        listOf(
                            text("字号:\n", "#666666".toColor()),
                            text(nameTaskBean?.companyWord ?: "")
                        )
                    )
                },
                textFlow {
                    setGridIndex(1, 4)
                    //text = "行业用语:\n${nameTaskBean?.termsWord}"
                    resetChildren(
                        listOf(
                            text("行业用语:\n", "#666666".toColor()),
                            text(nameTaskBean?.termsWord ?: "")
                        )
                    )
                },
                textFlow {
                    setGridIndex(1, 5)
                    resetChildren(
                        listOf(
                            text("任务状态:\n", Color.DEEPSKYBLUE),
                            text(
                                when (nameTaskBean?.taskState) {
                                    NameTaskBean.STATE_NORMAL -> "就绪"
                                    NameTaskBean.STATE_RUNNING -> "运行中"
                                    NameTaskBean.STATE_FINISH -> "已完成"
                                    else -> "--"
                                }
                            )
                        )
                    )
                }
            ))
        }
    }
}