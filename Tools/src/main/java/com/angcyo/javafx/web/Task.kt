package com.angcyo.javafx.web

import com.angcyo.core.component.file.writeText
import com.angcyo.http.base.fromJson
import com.angcyo.http.base.listType
import com.angcyo.http.base.toJson
import com.angcyo.javafx.base.ex.ctl
import com.angcyo.javafx.bean.NameTaskBean
import com.angcyo.javafx.controller.TipController
import com.angcyo.javafx.controller.dslTip
import com.angcyo.javafx.controller.main.TabDebugController
import com.angcyo.javafx.controller.main.TabLogController
import com.angcyo.javafx.controller.main.appendLog
import com.angcyo.library.ex.getResourceAsStream
import com.angcyo.library.ex.readText
import com.angcyo.log.L
import com.angcyo.selenium.auto.AutoControl
import com.angcyo.selenium.bean.TaskBean
import java.io.File

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/12/30
 */
object Task {

    //当前正在执行的控制
    var _currentControl: AutoControl? = null

    /**启动任务*/
    fun start(task: TaskBean) {
        //自动控制器
        val control = AutoControl()
        _currentControl = control

        //日志输出
        control.logAction = {
            L.wt(it)
            ctl<TabLogController>()?.appendLog(it)
        }

        //提示信息输出
        control.tipAction = { tip ->
            tip.toString().apply {
                L.wt(this)
                appendLog(this)
            }
            dslTip(tip)
        }

        //监听任务执行状态
        control._controlState.addListener { observable, oldValue, newValue ->
            if (newValue == AutoControl.STATE_FINISH) {
                ctl<TabDebugController>()?.enableStartNode(true)
            }
            //更新控制图标
            ctl<TipController>()?.updateImageByState(newValue)
        }

        //启动
        control.start(task)
    }

    /**从资源文件夹中, 获取[TaskBean]*/
    fun getResTask(resName: String): TaskBean? {
        return getResourceAsStream(resName)?.bufferedReader()?.readText()?.fromJson()
    }

    val nameTaskList = mutableListOf<NameTaskBean>()
    fun addNameTaskBean(bean: NameTaskBean) {
        nameTaskList.add(0, bean)
        File("./json/name_task_list.json").writeText(nameTaskList.toJson {
            setPrettyPrinting()
        }, false)
    }

    fun readNameTaskList() {
        File("./json/name_task_list.json").readText()?.fromJson<List<NameTaskBean>>(listType(NameTaskBean::class.java))
            ?.let {
                nameTaskList.clear()
                nameTaskList.addAll(it)
            }
    }
}