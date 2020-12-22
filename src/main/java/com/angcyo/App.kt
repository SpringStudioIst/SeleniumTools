package com.angcyo

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage

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
        this.primaryStage = primaryStage
        app = this

        val root = FXMLLoader.load<Parent>(javaClass.classLoader.getResource("main.fxml")) //com.angcyo/main.fxml
        primaryStage.title = "Hello JavaFX"
        //primaryStage.setIconified(true);//最小化
        //primaryStage.isAlwaysOnTop = true //置顶
        primaryStage.scene = Scene(root, 300.0, 275.0)
        //primaryStage.setOpacity(0.1);
        //primaryStage.setMaximized(true);//最大化
        //primaryStage.toBack();
        //primaryStage.setFullScreen(true);
        primaryStage.show()
    }

}

