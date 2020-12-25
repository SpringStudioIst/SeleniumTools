package com.angcyo.javafx

import com.angcyo.javafx.base.L
import com.angcyo.javafx.base.getResource
import javafx.application.Application
import javafx.application.Platform
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.stage.Stage
import javafx.stage.StageStyle

/**
 * Email:angcyo@126.com
 *
 * @author angcyo
 * @date 2020/12/22
 */
class App : Application() {

    companion object {
        lateinit var app: App
        fun runApp(vararg args: String) {
            launch(App::class.java, *args)
        }
    }

    lateinit var primaryStage: Stage

    override fun start(primaryStage: Stage) {
        //JavaFX Application Thread
        this.primaryStage = primaryStage
        app = this

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
    }

    init {
        //JavaFX Application Thread
        L.init("JavaFx", true)
    }

    override fun init() {
        super.init()
        //JavaFX-Launcher
        Platform.runLater {

        }
    }

    override fun stop() {
        super.stop()
        //JavaFX Application Thread
        Platform.exit()
    }

}

