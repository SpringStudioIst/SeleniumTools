package com.angcyo.javafx.controller.main

import com.angcyo.http.base.fromJson
import com.angcyo.http.base.toJson
import com.angcyo.javafx.base.BaseController
import com.angcyo.javafx.base.ex.ctl
import com.angcyo.javafx.base.ex.findByCss
import com.angcyo.javafx.base.ex.getImageFx
import com.angcyo.javafx.base.ex.onMain
import com.angcyo.javafx.controller.MainController
import com.angcyo.javafx.ui.dslAlert
import com.angcyo.javafx.ui.saveOnTextChanged
import com.angcyo.javafx.ui.switchById
import com.angcyo.javafx.web.Task
import com.angcyo.library.ex.getResourceAsStream
import com.angcyo.library.ex.readText
import com.angcyo.selenium.DslSelenium
import com.angcyo.selenium.bean.ActionBean
import com.angcyo.selenium.bean.TaskBean
import javafx.scene.control.*
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

    companion object {
        const val ACTION_PATH = "./json/action.json"
        const val TASK_PATH = "./json/task.json"
    }

    override fun initialize(stage: Stage?, location: URL?, resources: ResourceBundle?) {
        super.initialize(stage, location, resources)
        startNode = stage?.findByCss("#startNode")
        taskTextNode = stage?.findByCss("#taskTextNode")

        startNode?.setOnAction {
            if (DslSelenium.checkDriver()) {
                val taskBean = taskTextNode?.text?.fromJson<TaskBean>()
                if (taskBean == null) {
                    dslAlert {
                        alertType = Alert.AlertType.ERROR
                        icons.add(getImageFx("logo.png")!!)
                        contentText = "数据格式错误!"
                    }
                } else {
                    startNode?.isDisable = true
                    Task.start(taskBean)
                }
            } else {
                dslAlert {
                    alertType = Alert.AlertType.ERROR
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

        updateTaskText(TASK_PATH.readText() ?: getResourceAsStream("amr_task.json")?.bufferedReader()?.readText())
        taskTextNode?.saveOnTextChanged(TASK_PATH)

        _initTestNode(stage)
    }

    /**激活开始按钮*/
    fun enableStartNode(enable: Boolean) {
        onMain {
            startNode?.isDisable = !enable
        }
    }

    fun updateTaskText(string: String?) {
        taskTextNode?.text = string
    }

    //测试界面
    fun _initTestNode(stage: Stage?) {
        val startActionNode: Button? = stage?.findByCss("#startActionNode")
        val actionAreaNode: TextArea? = stage?.findByCss("#actionAreaNode")

        //丢失焦点之后, 格式化json
        actionAreaNode?.focusedProperty()?.addListener { observable, oldValue, newValue ->
            if (!newValue) {
                actionAreaNode.text?.fromJson<ActionBean>()?.apply {
                    actionAreaNode.text = this.toJson {
                        setPrettyPrinting()
                    }
                }
            }
        }

        startActionNode?.setOnAction {
            //插入一个指定的ActionBean, 并执行
            val actionJson = actionAreaNode?.text
            val actionBean: ActionBean? = actionJson?.fromJson()
            actionBean?.let {
                Task._currentControl?.actionRunSchedule?.addNextAction(it)

                //恢复启动
                Task._currentControl?.resume()
            }
        }

        actionAreaNode?.text = ACTION_PATH.readText()
        actionAreaNode?.saveOnTextChanged(ACTION_PATH)
    }

}