package com.angcyo.javafx.controller.main

import com.angcyo.core.component.file.writeTo
import com.angcyo.http.base.toJson
import com.angcyo.javafx.app
import com.angcyo.javafx.base.BaseController
import com.angcyo.javafx.base.ex.findByCss
import com.angcyo.javafx.bean.AppConfigBean
import com.angcyo.javafx.ui.chooserFile
import com.angcyo.javafx.ui.ext
import javafx.scene.control.Button
import javafx.scene.control.TextField
import javafx.stage.Stage
import java.io.File
import java.net.URL
import java.util.*

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/12/29
 */
class TabConfigController : BaseController() {

    /**选择驱动所在的目录*/
    var selectorDriverNode: Button? = null

    var driverEditNode: TextField? = null

    companion object {
        const val CONFIG_PATH = "./config/config.json"

        fun saveConfig(appConfigBean: AppConfigBean) {
            appConfigBean.toJson().writeTo(File(CONFIG_PATH))
        }
    }

    override fun initialize(stage: Stage?, location: URL?, resources: ResourceBundle?) {
        super.initialize(stage, location, resources)
        selectorDriverNode = stage?.findByCss("#selectorDriverNode")
        driverEditNode = stage?.findByCss("#driverEditNode")

        val appConfigBean = app().appConfigBean
        driverEditNode?.text = appConfigBean.driverPath
        selectorDriverNode?.setOnAction {
            chooserFile("选择对应版本的驱动程序", ext = ext("驱动程序(*.exe)", "*.exe"))?.let {
                driverEditNode?.text = it.absolutePath
                appConfigBean.driverPath = it.absolutePath
                saveConfig(appConfigBean)
            }
        }
    }
}