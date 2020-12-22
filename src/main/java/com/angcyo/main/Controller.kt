package com.angcyo.main

import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Button
import java.net.URL
import java.util.*

/**
 * [@FXML] 注解可以省略, 为了标识. 还是保留
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/12/22
 */
class Controller : Initializable {

    @FXML
    lateinit var button: Button

    @FXML
    fun handleButtonClick() {
        button.text = "test"
    }

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        println("initialize...$location $resources")
    }

}