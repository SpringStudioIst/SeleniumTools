package com.angcyo.javafx.controller

import com.angcyo.javafx.App
import com.angcyo.javafx.BaseApp.Companion.app
import com.angcyo.javafx.app
import com.angcyo.javafx.base.BaseController
import com.angcyo.javafx.base.ex.ctl
import com.angcyo.javafx.base.ex.getStage
import com.angcyo.javafx.base.ex.onLater
import com.angcyo.javafx.base.ex.onMain
import com.angcyo.javafx.controller.main.*
import com.angcyo.library.ex.isFileExist
import com.angcyo.library.ex.nowTimeString
import javafx.fxml.FXML
import javafx.scene.control.*
import okhttp3.internal.platform.Platform
import java.io.File
import java.net.URL
import java.util.*

/**
 * [@FXML] 注解可以省略, 为了标识. 还是保留
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/12/22
 */
class MainController : BaseController() {

    val tabHomeController = TabHomeController()
    val tabNameController = TabNameController()
    val tabConfigController = TabConfigController()
    val tabLogController = TabLogController()
    val tabDebugController = TabDebugController()

    /**底部提示条*/
    @FXML
    lateinit var bottomTipNode: Label

    /**关闭菜单*/
    lateinit var closeMenu: MenuItem

    /**置顶窗口*/
    lateinit var topMenuItem: RadioMenuItem

    /**主菜单*/
    lateinit var mainMenuBar: MenuBar

    /**Tab切换*/
    lateinit var mainTabNode: TabPane

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        super.initialize(location, resources)

        topMenuItem.isSelected = App.isAlwaysOnTopProperty.get()
        topMenuItem.selectedProperty().addListener { observable, oldValue, newValue ->
            ctl<TabConfigController>()?.alwaysTop(newValue)
        }

        onLater {
            stage = bottomTipNode.getStage()
            tabHomeController.initialize(stage, location, resources)
            tabNameController.initialize(stage, location, resources)
            tabConfigController.initialize(stage, location, resources)
            tabLogController.initialize(stage, location, resources)
            tabDebugController.initialize(stage, location, resources)

            tabLogController.appendLog(buildString {
                appendLine(Platform.get().toString())
                System.getProperties().forEach { entry ->
                    appendLine("${entry.key}->${System.getProperty(entry.key.toString())}")
                }

                val key = "angcyo.config"
                appendLine("${key}->${System.getProperty(key)}")
                //appendln(System.getProperty("os.name"))
                //appendln(System.getProperty("os.version"))
                //appendln(System.getProperty("os.arch"))
            })

            /*https://blog.csdn.net/weter_drop/article/details/108307593*/
            tabLogController.appendLog(buildString {
                System.getenv().forEach { entry ->
                    appendLine("${entry.key}->${entry.value}")
                }
            })

            tabLogController.appendLog("启动参数:${app().parameters?.raw}")

            tabLogController.appendLog("运行目录:${File("").absolutePath}")

            checkDriver()
        }

        //tip
        bottomTipNode.text = "就绪!"

        //menu
        //mainMenuBar.isUseSystemMenuBar = true
        closeMenu.setOnAction {
            app.exit()
        }
    }

    /**更新底部提示*/
    fun bottomTip(text: String?) {
        onMain {
            bottomTipNode.text = "${nowTimeString()} $text"
        }
    }

    /**检查驱动程序*/
    fun checkDriver() {
        if (app().appConfigBean.driverPath.isFileExist()) {
            bottomTip("驱动程序:${app().appConfigBean.driverPath}".apply {
                appendLog(this)
            })
        } else {
            bottomTip("驱动程序不存在, 请在[配置]页面, 设置驱动程序.".apply {
                appendLog(this)
            })
        }
    }
}

fun showBottomTip(text: String?) {
    ctl<MainController>()?.bottomTip(text)
}

fun showBottomTipAndAppendLog(text: String?) {
    showBottomTip(text)
    appendLog(text)
}