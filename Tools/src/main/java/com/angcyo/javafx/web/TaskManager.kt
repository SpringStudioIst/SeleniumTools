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
import com.angcyo.library.ex.resetAll
import com.angcyo.log.L
import com.angcyo.selenium.auto.AutoControl
import com.angcyo.selenium.bean.ActionBean
import com.angcyo.selenium.bean.CheckBean
import com.angcyo.selenium.bean.TaskBean
import java.io.File

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/12/30
 */
object TaskManager {

    //当前正在执行的控制
    var _currentControl: AutoControl? = null

    fun init() {
        checkList.resetAll(readResCheckList())
        backActionList.resetAll(readResBackActionList())
        debugTaskList.resetAll(readResTaskList())
        readNameTaskList()
    }

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

    //<editor-fold desc="Task">

    val debugTaskList = mutableListOf<TaskBean>()
    fun readResTaskList(): List<TaskBean> {
        val result = mutableListOf<TaskBean>()
        readResTask("all_nz_task.json")?.let { result.add(it.initCheck()) }
        readResTask("all_gth_task.json")?.let { result.add(it.initCheck()) }
        readResTask("all_nz_mp_task.json")?.let { result.add(it.initCheck()) }
        readResTask("all_gth_mp_task.json")?.let { result.add(it.initCheck()) }
        return result
    }

    /**从资源文件夹中, 获取[TaskBean]*/
    fun readResTask(resName: String): TaskBean? {
        return getResourceAsStream(resName)?.bufferedReader()?.readText()?.fromJson()
    }

    val nameTaskList = mutableListOf<NameTaskBean>()
    fun addNameTaskBean(bean: NameTaskBean) {
        nameTaskList.add(0, bean)
        saveNameTask()
    }

    fun saveNameTask() {
        File("./json/name_task_list.json").writeText(nameTaskList.toJson {
            setPrettyPrinting()
        }, false)
    }

    /**读取一键核名保存的任务列表*/
    fun readNameTaskList() {
        File("./json/name_task_list.json").readText()?.fromJson<List<NameTaskBean>>(listType(NameTaskBean::class.java))
            ?.let {
                nameTaskList.clear()
                nameTaskList.addAll(it)
            }
    }

    //</editor-fold desc="Task">

    //<editor-fold desc="check">

    /**保存所有[CheckBean]*/
    val checkList = mutableListOf<CheckBean>()
    fun readResCheckList(): List<CheckBean> {
        val result = mutableListOf<CheckBean>()
        fun addBean(checkBean: CheckBean) {
            if (checkBean.checkId > 0 && result.find { it.checkId == checkBean.checkId } != null) {
                //已经存在
            } else {
                result.add(checkBean)
            }
        }
        readResCheck("check_common.json")?.forEach { addBean(it) }
        readResCheck("check_nz.json")?.forEach { addBean(it) }
        readResCheck("check_gth.json")?.forEach { addBean(it) }
        return result
    }

    /**从资源文件夹中, 获取[CheckBean]*/
    fun readResCheck(resName: String): List<CheckBean>? {
        return getResourceAsStream(resName)?.bufferedReader()?.readText()
            ?.fromJson<List<CheckBean>>(listType(CheckBean::class.java))
    }

    //</editor-fold desc="check">

    //<editor-fold desc="back action">

    /**保存所有的back[ActionBean]*/
    val backActionList = mutableListOf<ActionBean>()
    fun readResBackActionList(): List<ActionBean> {
        val result = mutableListOf<ActionBean>()
        fun addBean(actionBean: ActionBean) {
            if (actionBean.actionId > 0 && result.find { it.actionId == actionBean.actionId } != null) {
                //已经存在
            } else {
                result.add(actionBean)
            }
        }
        readResBackAction("back_action.json")?.forEach { addBean(it) }
        return result
    }

    /**从资源文件夹中, 获取[ActionBean]*/
    fun readResBackAction(resName: String): List<ActionBean>? {
        return getResourceAsStream(resName)?.bufferedReader()?.readText()
            ?.fromJson<List<ActionBean>>(listType(ActionBean::class.java))
    }

    //</editor-fold desc="back action">
}

/**初始化任务中的[CheckBean]*/
fun TaskBean.initCheck(): TaskBean {
    before?.initCheck()
    actionList?.initCheck()
    after?.initCheck()
    backActionList = if (backActionList == null) {
        TaskManager.backActionList.toMutableList()
    } else {
        backActionList?.toMutableList()?.apply {
            addAll(TaskManager.backActionList)
        }
    }
    backActionList?.initCheck()
    return this
}

fun List<ActionBean>.initCheck() {
    forEach { action ->
        action.initCheck()
    }
}

fun ActionBean.initCheck() {
    if (check == null) {
        before?.initCheck()
        check = getCheckById(checkId)
        after?.initCheck()
    }
}

fun getCheckById(checkId: Long) = TaskManager.checkList.find { check -> check.checkId == checkId }
