package com.angcyo.javafx.base

import com.angcyo.http.rx.doBack
import javafx.application.Platform
import java.net.URL

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/12/24
 */

//<editor-fold desc="Java平台">

fun Any?.hash(): String? {
    return this?.hashCode()?.run { Integer.toHexString(this) }
}

fun Any.simpleHash(): String {
    return "${this.javaClass.simpleName}(${this.hash()})"
}

fun Any.simpleClassName(): String {
    return this.javaClass.simpleName
}

fun Any.className(): String {
    if (this is Class<*>) {
        return this.name
    }
    return this.javaClass.name
}

fun Any?.str(): String {
    return if (this is String) {
        this
    } else {
        this.toString()
    }
}

/**[CharSequence]中是否包含指定[text]*/
fun CharSequence?.have(text: CharSequence): Boolean {
    if (this == null) {
        return false
    }
    return this.str() == text.str() || this.contains(text.toString().toRegex())
}

//</editor-fold desc="Java平台">

//<editor-fold desc="JavaFX平台">

/**获取jar中的资源*/
fun Any.getResource(name: String): URL? {
    var url: URL? = null
    try {
        url = javaClass.getResource(name)
    } catch (e: Exception) {
        //
    }
    if (url == null) {
        url = javaClass.classLoader.getResource(name)
    }
    return url
}

/**在主线程运行, 通常用来在子线程调用修改UI
 * Not on FX application thread; currentThread = Thread-3
 * */
fun onMain(action: () -> Unit) {
    if (isFxApplicationThread()) {
        action()
    } else {
        Platform.runLater { action() }
    }
}

fun onBack(action: () -> Unit) {
    doBack(action)
}

/**是否是主进程
 * JavaFX Application Thread
 * */
fun isFxApplicationThread() = Platform.isFxApplicationThread()

/**获取控制器*/
inline fun <reified Controller : BaseController> ctl(): Controller? =
    BaseController.controllerHolder[Controller::class.java] as? Controller

//</editor-fold desc="JavaFX平台">