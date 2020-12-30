package com.angcyo.javafx.controller

import com.angcyo.javafx.base.ex.ctl
import com.angcyo.javafx.base.ex.onMain
import com.angcyo.library.ex.getResource
import com.angcyo.log.L
import com.angcyo.selenium.auto.ControlTip
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Screen
import javafx.stage.Stage
import javafx.stage.StageStyle

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/12/28
 */
object Tip {
    var tipStage: Stage? = null

    /**显示提示舞台*/
    fun show(tip: ControlTip) {
        if (tipStage == null) {
            //首次显示

            val stage = Stage(StageStyle.UTILITY)
            stage.isResizable = false

            val root = FXMLLoader.load<Parent>(getResource("Tip.fxml"))
            stage.scene = Scene(root)
            stage.isAlwaysOnTop = true

            stage.setOnCloseRequest {
                L.i(it)
                it.consume()
                tipStage?.close()
            }
            stage.setOnShown {
                L.i(it)
            }
            stage.setOnShowing {
                L.i(it)
            }
            stage.setOnHidden {
                //2:隐藏了
                L.i(it)
            }
            stage.setOnHiding {
                //1:隐藏中
                L.i(it)
            }

            stage.show()
            //调整位置, 到左下角
            onMain {
                Screen.getPrimary().apply {
                    stage.x = 20.0
                    stage.y = visualBounds.maxY - 20 - stage.height
                }
            }
            tipStage = stage
        }
        tip.des?.let { ctl<TipController>()?.setDes(it) }
        tip.title?.let { ctl<TipController>()?.setTitle(it) }
        if (tip.nextTime > 0) {
            ctl<TipController>()?.showProgress(tip.nextTime)
        }
        tipStage?.sizeToScene()
        tipStage?.show()
    }

    /**隐藏提示舞台*/
    fun hide() {
        tipStage?.hide()
    }
}

fun dslTip(action: ControlTip.() -> Unit) {
    val tip = ControlTip()
    tip.action()
    onMain { Tip.show(tip) }
}

fun dslTip(tip: ControlTip) {
    onMain { Tip.show(tip) }
}