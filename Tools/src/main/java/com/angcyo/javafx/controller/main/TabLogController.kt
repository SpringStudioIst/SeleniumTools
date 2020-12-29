package com.angcyo.javafx.controller.main

import com.angcyo.javafx.base.BaseController
import com.angcyo.javafx.base.ex.findByCss
import com.angcyo.library.ex.nowTimeString
import javafx.scene.control.TextArea
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
    }

    /**追加日志*/
    fun appendLog(log: String) {
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