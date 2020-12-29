package com.angcyo.javafx

import com.angcyo.javafx.ui.Tray
import com.angcyo.library.ex.getImage
import com.angcyo.library.ex.getResource
import com.angcyo.log.L
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.stage.Stage
import javafx.stage.StageStyle
import java.io.File

/**
 * Email:angcyo@126.com
 *
 * @author angcyo
 * @date 2020/12/22
 */
class App : BaseApp() {

    override fun start(primaryStage: Stage) {
        super.start(primaryStage)

        primaryStage.initStyle(StageStyle.DECORATED)
        primaryStage.icons.add(Image(getResource("logo.png").toString()))

        val root = FXMLLoader.load<Parent>(javaClass.classLoader.getResource("main.fxml")) //com.angcyo/main.fxml
        primaryStage.title = "Hello JavaFX"
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

        Tray.addTray(getImage("logo.png")!!, "SeleniumTools 2020-12-29")
    }
}

