package com.angcyo.javafx.controller.main

import com.angcyo.javafx.base.BaseController
import com.angcyo.javafx.base.ex.findByCss
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
            engine?.load("https://www.baidu.com")
        }
    }
}