package com.angcyo.javafx.controller.main

import com.angcyo.javafx.base.BaseController
import com.angcyo.javafx.base.ex.copy
import com.angcyo.javafx.base.ex.ctl
import com.angcyo.javafx.base.ex.findByCss
import com.angcyo.javafx.base.ex.onMain
import com.angcyo.javafx.ui.*
import com.angcyo.library.ex.nowTimeString
import javafx.scene.control.TextArea
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.stage.Stage
import java.net.URL
import java.util.*

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/12/29
 */
class TabLogController : BaseController() {

    /**日志输出文本域*/
    var logTextNode: TextArea? = null

    override fun initialize(stage: Stage?, location: URL?, resources: ResourceBundle?) {
        super.initialize(stage, location, resources)
        logTextNode = stage?.findByCss("#logTextNode")

        //context menu
        logTextNode?.apply {
            contextMenu = contextMen {
                listOf(menuItem("保存") {
                    saveLog()
                }, menuItem("清除") {
                    logTextNode?.text = ""
                }, menuItem("复制") {
                    logTextNode?.text?.copy()
                }, menuItem("全选") {
                    logTextNode?.selectAll()
                })
            }
        }

        //key save
        logTextNode?.setOnKeyPressed {
            if (it.match(KeyCode.S, KeyCodeCombination.CONTROL_DOWN)) {
                saveLog()
            }
        }
    }

    fun saveLog() {
        //保存
        val text = logTextNode?.text
        text?.let {
            dslSaveFile {
                title = "保存日志"
                extList.add(ext("Log文件", "*.log"))
                extList.add(ext("所有文件", "*.*"))
            }?.let {
                it.writeText(text)
            }
        }
    }

    /**追加日志*/
    fun appendLog(log: String) {
        val threadName = Thread.currentThread().name
        onMain {
            logTextNode?.apply {
                val old = if (text.length > 1 * 1024 * 1024) "" else text
                text = buildString {
                    if (!old.isNullOrEmpty()) {
                        appendLine(old)
                    }
                    appendLine("${nowTimeString()} [${threadName}]↓")
                    appendLine(log)
                }
                //滚动到底部
                positionCaret(text.length)
            }
        }
    }
}

fun appendLog(log: String?) {
    ctl<TabLogController>()?.appendLog(log ?: "--")
}