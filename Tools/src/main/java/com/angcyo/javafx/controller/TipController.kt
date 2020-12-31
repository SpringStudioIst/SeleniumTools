package com.angcyo.javafx.controller

import com.angcyo.javafx.base.BaseController
import com.angcyo.javafx.base.ex.ctl
import com.angcyo.javafx.base.ex.onMain
import com.angcyo.javafx.ui.addKeyFrame
import com.angcyo.javafx.ui.dslTimeline
import com.angcyo.javafx.ui.setImageResource
import com.angcyo.javafx.web.Task
import com.angcyo.log.L
import com.angcyo.selenium.auto.isControlEnd
import com.angcyo.selenium.auto.isControlPause
import com.angcyo.selenium.auto.isControlStart
import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.control.ProgressBar
import javafx.scene.image.ImageView
import javafx.scene.layout.AnchorPane
import java.net.URL
import java.util.*


/**
 * 操作提示界面控制器
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/12/27
 */
class TipController : BaseController() {

    @FXML
    lateinit var rootNode: AnchorPane

    @FXML
    lateinit var desNode: Label

    @FXML
    lateinit var titleNode: Label

    @FXML
    lateinit var controlImageNode: ImageView

    lateinit var progressBarNode: ProgressBar

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        super.initialize(location, resources)
        //rootNode.scene.window


        Task._currentControl?._controlState?.get()?.apply {
            updateImageByState(this)
        }

        //按钮
        controlImageNode.setOnMouseClicked {
            Task._currentControl?.apply {
                when {
                    _controlState.get().isControlPause() -> resume()
                    _controlState.get().isControlStart() -> pause()
                    _currentTaskBean != null -> start(_currentTaskBean!!)
                    else -> L.w("无任务需要执行!")
                }
            }
        }
    }

    fun setDes(des: String? = null) {
        onMain {
            desNode.text = des
        }
    }

    fun setTitle(title: String? = null) {
        onMain {
            titleNode.text = title
        }
    }

    fun showProgress(time: Long) {
        onMain {
            dslTimeline {
                addKeyFrame(0.0, progressBarNode.progressProperty(), 0)
                addKeyFrame(time.toDouble(), progressBarNode.progressProperty(), 1)
            }
        }
    }

    fun updateImageByState(state: Number) {
        if (state.isControlPause() || state.isControlEnd()) {
            ctl<TipController>()?.updateImage("play_#3246F0.png")
        } else if (state.isControlStart()) {
            ctl<TipController>()?.updateImage("pause_#3246F0.png")
        }
    }

    fun updateImage(name: String) {
        onMain {
            controlImageNode.setImageResource(name)
        }
    }
}