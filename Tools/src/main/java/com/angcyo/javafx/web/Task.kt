package com.angcyo.javafx.web

import com.angcyo.javafx.controller.dslTip
import com.angcyo.log.L
import com.angcyo.selenium.auto.AutoControl
import com.angcyo.selenium.bean.TaskBean
import org.openqa.selenium.edge.EdgeDriver

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
        control.logAction = { tip ->
            L.wt(tip.toString())
            dslTip(tip)
        }

        //启动
        control.start(task)
    }
}