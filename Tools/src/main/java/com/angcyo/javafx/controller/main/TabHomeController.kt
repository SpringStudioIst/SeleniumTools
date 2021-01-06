package com.angcyo.javafx.controller.main

import com.angcyo.javafx.base.BaseController
import com.angcyo.javafx.base.ex.findByCss
import com.angcyo.log.L
import javafx.concurrent.Worker
import javafx.scene.web.WebView
import javafx.stage.Stage
import java.net.URL
import java.util.*

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/01/04
 */
class TabHomeController : BaseController() {

    override fun initialize(stage: Stage?, location: URL?, resources: ResourceBundle?) {
        super.initialize(stage, location, resources)

        //webview
        val webView: WebView? = stage?.findByCss("#homeWebView")
        webView?.apply {
            //Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/606.1 (KHTML, like Gecko) JavaFX/11 Safari/606.1
            //engine.userAgent
            //engine.isJavaScriptEnabled = true

            engine.loadWorker.stateProperty().addListener { observable, oldValue, newValue ->
                L.i("state to:$newValue title:${engine.title}")

                if (newValue == Worker.State.SUCCEEDED) {
                    //DevTools.show(webView)
                }
            }
            engine?.load("https://www.baidu.com")
        }
    }
}