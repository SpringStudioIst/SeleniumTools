package com.angcyo.javafx.controller.main

import com.angcyo.javafx.SystemSoftware
import com.angcyo.javafx.base.BaseController
import com.angcyo.javafx.base.ex.findByCss
import com.angcyo.javafx.base.ex.onDelay
import com.angcyo.javafx.controller.Tip
import com.angcyo.library.ex.getResourceAsStream
import com.angcyo.library.ex.nowTime
import com.angcyo.log.L
import javafx.scene.control.Button
import javafx.scene.control.TextArea
import javafx.scene.control.Tooltip
import javafx.stage.Stage
import java.net.URL
import java.util.*

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/12/29
 */
class TabHomeController : BaseController() {

    /**启动按钮*/
    var startNode: Button? = null

    var taskTextNode: TextArea? = null

    override fun initialize(stage: Stage?, location: URL?, resources: ResourceBundle?) {
        super.initialize(stage, location, resources)
        startNode = stage?.findByCss("#startNode")
        taskTextNode = stage?.findByCss("#taskTextNode")

        startNode?.setOnAction {
            //testSeleniumhq()

//            Popup().apply {
//                content.add(bottomTipLabel)
//                show(startButton.scene.window)
//            }

            //startButton.scene.window.hide()

            Tip.show("des", "title")

            onDelay(2_000) {
                Tip.show("des${nowTime()}", "title")
            }


        }
        startNode?.tooltip = Tooltip("启动任务")

        updateTaskText(getResourceAsStream("amr_task.json")?.bufferedReader()?.readText())
    }

    fun updateTaskText(string: String?) {
        taskTextNode?.text = string
    }

}