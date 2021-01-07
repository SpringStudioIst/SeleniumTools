package com.angcyo.javafx.controller

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
import com.angcyo.log.L
import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.control.MenuBar
import javafx.scene.control.MenuItem
import javafx.scene.control.TabPane
import okhttp3.internal.platform.Platform
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

    /**主菜单*/
    lateinit var mainMenuBar: MenuBar

    /**Tab切换*/
    lateinit var mainTabNode: TabPane

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        super.initialize(location, resources)
        onLater {
            tabHomeController.initialize(bottomTipNode.getStage(), location, resources)
            tabNameController.initialize(bottomTipNode.getStage(), location, resources)
            tabConfigController.initialize(bottomTipNode.getStage(), location, resources)
            tabLogController.initialize(bottomTipNode.getStage(), location, resources)
            tabDebugController.initialize(bottomTipNode.getStage(), location, resources)

            tabLogController.appendLog(buildString {
                appendLine(Platform.get().toString())
                System.getProperties().forEach { entry ->
                    appendLine("${entry.key}->${System.getProperty(entry.key.toString())}".apply { L.i(this) })
                }
                //appendln(System.getProperty("os.name"))
                //appendln(System.getProperty("os.version"))
                //appendln(System.getProperty("os.arch"))
            })

            checkDriver()
        }

        //tip
        bottomTipNode.text = "就绪!"

        //menu
        mainMenuBar.isUseSystemMenuBar = true
        closeMenu.setOnAction {
            app.exit()
        }
    }

    /**更新底部提示*/
    fun bottomTip(text: String) {
        onMain {
            bottomTipNode.text = "${nowTimeString()} $text"
        }
    }

    /**检查驱动程序*/
    fun checkDriver() {
        if (app().appConfigBean.driverPath.isFileExist()) {
            bottomTip("驱动程序:${app().appConfigBean.driverPath}")
        } else {
            bottomTip("驱动程序不存在, 请在[配置]页面, 设置驱动程序.")
        }
    }
}

fun showBottomTip(text: String) {
    ctl<MainController>()?.bottomTip(text)
}