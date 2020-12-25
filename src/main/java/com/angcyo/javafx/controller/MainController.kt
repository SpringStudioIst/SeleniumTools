package com.angcyo.javafx.controller

import com.angcyo.javafx.base.L
import com.angcyo.javafx.base.onMain
import com.angcyo.javafx.web.WebControl
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Button
import javafx.scene.control.Label
import okhttp3.internal.platform.Platform
import org.openqa.selenium.PageLoadStrategy
import org.openqa.selenium.edge.EdgeDriver
import org.openqa.selenium.edge.EdgeOptions
import java.net.URL
import java.util.*

/**
 * [@FXML] 注解可以省略, 为了标识. 还是保留
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/12/22
 */
class MainController : Initializable {

    @FXML
    lateinit var startButton: Button

    @FXML
    lateinit var bottomTipLabel: Label

    @FXML
    lateinit var textTipLabel: Label

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        L.i("initialize...$location $resources")

        bottomTipLabel.text = "就绪!"
        textTipLabel.text = buildString {
            appendln(Platform.get().toString())
            System.getProperties().forEach { entry ->
                appendln("${entry.key}->${System.getProperty(entry.key.toString())}".apply { L.i(this) })
            }
            //appendln(System.getProperty("os.name"))
            //appendln(System.getProperty("os.version"))
            //appendln(System.getProperty("os.arch"))
        }

        startButton.setOnAction {
            testSeleniumhq()
        }
    }

    fun testSeleniumhq() {
        System.setProperty("webdriver.edge.driver", "F:/FileDownloads/edgedriver_win64/msedgedriver.exe")
        val control = WebControl(EdgeDriver(EdgeOptions().apply {
            setPageLoadStrategy(PageLoadStrategy.EAGER)
        }))
        control.doAction()
    }

    fun bottomTip(text: String) {
        onMain {
            bottomTipLabel.text = text
        }
    }

}