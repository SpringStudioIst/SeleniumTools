package com.angcyo.javafx.controller

import com.angcyo.javafx.base.ex.ctl
import com.angcyo.javafx.base.ex.onMain
import com.angcyo.library.ex.getResource
import com.angcyo.log.L
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

    fun show(des: String? = null, title: String? = null) {
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
        des?.let { ctl<TipController>()?.setDes(it) }
        title?.let { ctl<TipController>()?.setTitle(it) }
        tipStage?.sizeToScene()
        tipStage?.show()
    }
}