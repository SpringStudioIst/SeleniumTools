package com.angcyo.javafx.web

import com.angcyo.selenium.auto.AutoControl
import com.angcyo.selenium.bean.TaskBean
import org.openqa.selenium.PageLoadStrategy
import org.openqa.selenium.edge.EdgeDriver
import org.openqa.selenium.edge.EdgeOptions

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/12/30
 */
object Task {

    /**启动任务*/
    fun start(task: TaskBean) {
        //驱动程序配置
        val options = EdgeOptions()
        task.config?.pageLoadStrategy?.let {
            PageLoadStrategy.fromString(it)?.let {
                options.setPageLoadStrategy(it)
            }
        }
        //自动控制器
        val control = AutoControl(EdgeDriver(options))
        control.start(task)
    }
}