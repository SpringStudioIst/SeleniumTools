package com.angcyo.javafx.base

import javafx.fxml.Initializable
import java.net.URL
import java.util.*

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/12/25
 */
open class BaseController : Initializable {

    companion object {
        val controllerHolder = hashMapOf<Class<*>, BaseController>()
    }

    /**初始化控制器*/
    override fun initialize(location: URL?, resources: ResourceBundle?) {
        L.i("${className()} initialize:$location $resources")
        controllerHolder[this.javaClass] = this
    }

    /**销毁控制器*/
    fun destroy() {
        L.i("${className()} destroy.")
        controllerHolder.remove(this.javaClass)
    }
}