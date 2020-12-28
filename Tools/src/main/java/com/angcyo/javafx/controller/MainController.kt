package com.angcyo.javafx.controller

import com.angcyo.javafx.base.BaseController
import com.angcyo.javafx.base.OSinfo
import com.angcyo.javafx.base.ex.onMain
import com.angcyo.javafx.web.WebControl
import com.angcyo.library.ex.getResource
import com.angcyo.log.L
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.Tooltip
import javafx.stage.Screen
import javafx.stage.Stage
import javafx.stage.StageStyle
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
class MainController : BaseController() {

    @FXML
    lateinit var startButton: Button

    @FXML
    lateinit var bottomTipLabel: Label

    @FXML
    lateinit var textTipLabel: Label

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        super.initialize(location, resources)
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
            //testSeleniumhq()

//            Popup().apply {
//                content.add(bottomTipLabel)
//                show(startButton.scene.window)
//            }

            //startButton.scene.window.hide()

            val stage = Stage(StageStyle.TRANSPARENT)
            Screen.getPrimary().apply {
                stage.x = 20.0
                stage.y = visualBounds.maxY - 20 - stage.height
            }

            val root = FXMLLoader.load<Parent>(getResource("main.fxml")) //com.angcyo/main.fxml
            stage.scene = Scene(root)
            stage.isAlwaysOnTop = true

            stage.show()

            onMain {
                L.i(stage.height)
                Screen.getPrimary().apply {
                    stage.x = 20.0
                    stage.y = visualBounds.maxY - 20 - stage.height
                }
            }
        }
        startButton.tooltip = Tooltip("tooltip")
    }

    fun testSeleniumhq() {
        if (OSinfo.isWindows) {
            System.setProperty("webdriver.edge.driver", "F:/FileDownloads/edgedriver_win64/msedgedriver.exe")
        } else if (OSinfo.isMacOSX) {
            System.setProperty("webdriver.edge.driver", "/Users/angcyo/Downloads/edgedriver_mac64/msedgedriver")
        }
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