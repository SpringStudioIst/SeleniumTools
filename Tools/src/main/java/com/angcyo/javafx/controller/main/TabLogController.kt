package com.angcyo.javafx.controller.main

import com.angcyo.javafx.base.BaseController
import com.angcyo.javafx.base.ex.ctl
import com.angcyo.javafx.base.ex.findByCss
import com.angcyo.javafx.base.ex.onMain
import com.angcyo.javafx.ui.dslSaveFile
import com.angcyo.javafx.ui.ext
import com.angcyo.javafx.ui.match
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

        //key save
        logTextNode?.setOnKeyPressed {
            if (it.match(KeyCode.S, KeyCodeCombination.CONTROL_DOWN)) {
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
        }
    }

    /**追加日志*/
    fun appendLog(log: String) {
        onMain {
            logTextNode?.apply {
                val old = text
                text = buildString {
                    if (!old.isNullOrEmpty()) {
                        appendLine(old)
                    }
                    appendLine("${nowTimeString()} ↓")
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