package com.angcyo.javafx.controller

import com.angcyo.javafx.base.BaseController
import javafx.fxml.FXML
import javafx.scene.control.Label
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
    lateinit var controlNode: ImageView

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        super.initialize(location, resources)
        rootNode.scene.window
    }
}