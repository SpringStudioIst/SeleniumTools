package com.angcyo.javafx

import com.angcyo.http.base.fromJson
import com.angcyo.javafx.base.ex.onBack
import com.angcyo.javafx.bean.AppConfigBean
import com.angcyo.javafx.controller.main.TabConfigController.Companion.CONFIG_PATH
import com.angcyo.javafx.http.HttpHelper
import com.angcyo.javafx.ui.Tray
import com.angcyo.javafx.ui.remove
import com.angcyo.javafx.web.TaskManager
import com.angcyo.library.ex.getImage
import com.angcyo.library.ex.getResource
import com.angcyo.library.ex.readText
import com.angcyo.log.L
import com.angcyo.selenium.DslSelenium
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.stage.Stage
import javafx.stage.StageStyle
import java.awt.TrayIcon
import java.io.File

/**
 * Email:angcyo@126.com
 *
 * @author angcyo
 * @date 2020/12/22
 */
class App : BaseApp() {

    var trayIcon: TrayIcon? = null

    var appConfigBean = AppConfigBean()

    companion object {
        const val NAME = "全自动辅助工具"
        const val VERSION = "2020-12-29"
        const val VERSION_CODE = 1
    }

    override fun start(primaryStage: Stage) {
        super.start(primaryStage)

        primaryStage.initStyle(StageStyle.DECORATED)
        primaryStage.icons.add(Image(getResource("logo.png").toString()))

        val root = FXMLLoader.load<Parent>(javaClass.classLoader.getResource("main.fxml")) //com.angcyo/main.fxml
        primaryStage.title = "$NAME $VERSION"
        //primaryStage.setIconified(true);//最小化
        //primaryStage.isAlwaysOnTop = true //置顶
        primaryStage.scene = Scene(root)
        //primaryStage.setOpacity(0.1);
        //primaryStage.setMaximized(true);//最大化
        //primaryStage.toBack();
        //primaryStage.setFullScreen(true);

        primaryStage.sizeToScene()
        primaryStage.show()

        L.i("根目录:" + File("").absolutePath)

        trayIcon = Tray.addTray(getImage("logo.png")!!, "$NAME $VERSION")

        //读取配置
        onBack {
            appConfigBean = File(CONFIG_PATH).readText()?.fromJson() ?: appConfigBean
            DslSelenium.initDriver(appConfigBean.driverPath)
            HttpHelper.init()
            TaskManager.init()
        }
    }

    override fun stop() {
        super.stop()
        trayIcon?.remove()
    }
}

fun app() = BaseApp.app as App