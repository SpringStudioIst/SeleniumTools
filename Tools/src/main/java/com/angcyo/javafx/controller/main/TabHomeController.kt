package com.angcyo.javafx.controller.main

import com.angcyo.javafx.base.BaseController
import com.angcyo.javafx.base.ex.ctl
import com.angcyo.javafx.base.ex.findByCss
import com.angcyo.javafx.base.ex.getImageFx
import com.angcyo.javafx.controller.MainController
import com.angcyo.javafx.ui.dslAlert
import com.angcyo.javafx.ui.switchById
import com.angcyo.library.ex.getResourceAsStream
import com.angcyo.selenium.DslSelenium
import javafx.scene.control.Button
import javafx.scene.control.ButtonType
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
            if (DslSelenium.checkDriver()) {
                //Task.start()
            } else {
                dslAlert {
                    icons.add(getImageFx("logo.png")!!)
                    contentText = "无效的驱动程序!"
                }?.let {
                    if (it.get() == ButtonType.OK) {
                        ctl<MainController>()?.mainTabNode?.switchById("configTab")
                    }
                }
            }
        }
        startNode?.tooltip = Tooltip("启动任务")

        updateTaskText(getResourceAsStream("amr_task.json")?.bufferedReader()?.readText())
    }

    fun updateTaskText(string: String?) {
        taskTextNode?.text = string
    }

}