package com.angcyo.javafx.controller.main

import com.angcyo.core.component.file.writeTo
import com.angcyo.http.base.toJson
import com.angcyo.javafx.app
import com.angcyo.javafx.base.BaseController
import com.angcyo.javafx.base.CancelRunnable
import com.angcyo.javafx.base.ex.*
import com.angcyo.javafx.bean.AppConfigBean
import com.angcyo.javafx.controller.MainController
import com.angcyo.javafx.http.HttpHelper
import com.angcyo.javafx.ui.*
import com.angcyo.selenium.DslSelenium
import javafx.scene.control.*
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
            appConfigBean.toJson().writeTo(File(CONFIG_PATH), false)
            DslSelenium.initDriver(appConfigBean.driverPath)
        }
    }

    override fun initialize(stage: Stage?, location: URL?, resources: ResourceBundle?) {
        super.initialize(stage, location, resources)
        selectorDriverNode = stage?.findByCss("#selectorDriverNode")
        driverEditNode = stage?.findByCss("#driverEditNode")

        val appConfigBean = app().appConfigBean
        driverEditNode?.text = appConfigBean.driverPath
        selectorDriverNode?.setOnAction {
            dslChooserFile {
                title = "选择对应版本的驱动程序"
                extList.addAll(
                    exts(
                        ext("驱动程序(*.exe)", "*.exe"),
                        ext("所有文件", "*.*")
                    )
                )
            }?.let {
                driverEditNode?.text = it.absolutePath

            }
        }
        driverEditNode?.textProperty()?.addListener { observable, oldValue, newValue ->
            appConfigBean.driverPath = newValue
            ctl<MainController>()?.checkDriver()
            saveConfig(appConfigBean)
        }

        //驱动下载
        fun initBin(css: String, url: String, enable: Boolean = true) {
            stage?.findByCss<ButtonBase>(css)?.apply {
                isDisable = !enable
                setOnAction {
                    openUrl(url)
                }
            }
        }
        initBin("#edgeBinLink", "https://developer.microsoft.com/en-us/microsoft-edge/tools/webdriver/")
        initBin("#chromeBinLink", "https://chromedriver.storage.googleapis.com/index.html", false)
        initBin("#huohuBinLink", "https://github.com/mozilla/geckodriver/releases", false)
        initBin("#operaBinLink", "https://github.com/operasoftware/operachromiumdriver/releases", false)
        initBin("#safariBinLink", "https://github.com/operasoftware/operachromiumdriver/releases", false)
        stage?.findByCss<ButtonBase>("#safariBinLink")?.apply {
            isDisable = true
            tooltip = Tooltip("内置")
        }

        //行业用语秒批更新
        val cookieFieldNode: TextField? = stage?.findByCss("#cookieFieldNode")
        val cookieUpdateNode: Button? = stage?.findByCss("#cookieUpdateNode")
        val cookieProgressBar: ProgressIndicator? = stage?.findByCss("#cookieProgressBar")
        cookieFieldNode?.textProperty()?.addListener { observable, oldValue, newValue ->
            cookieUpdateNode?.enable(!newValue.isNullOrEmpty())
        }
        fun switchToLoading(loading: Boolean) {
            cookieProgressBar.visible(loading)
            //cookieUpdateNode.visible(!loading)
            cookieUpdateNode.enable(!loading)
            cookieFieldNode.enable(!loading)
        }
        cookieUpdateNode?.setOnAction {
            switchToLoading(true)
            var lastRunnable: CancelRunnable? = null
            onBack {
                HttpHelper.loadIndustryList(cookieFieldNode?.text ?: "") { response, throwable ->
                    throwable?.let {
                        switchToLoading(false)
                        dslAlert {
                            alertType = Alert.AlertType.ERROR
                            icons.add(getImageFx("logo.png")!!)
                            contentText = "更新失败:$it"
                        }
                    }
                    response?.let {
                        lastRunnable?.isEnd = true
                        lastRunnable = onDelay(1_000) {
                            switchToLoading(false)
                            dslAlert {
                                icons.add(getImageFx("logo.png")!!)
                                contentText = "更新成功,共${HttpHelper.industryList.size}条!"
                                expandableContent = TextArea().apply {
                                    text = HttpHelper.industryList.toJson()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}