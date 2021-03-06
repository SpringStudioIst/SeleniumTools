package com.angcyo.javafx.controller.main

import com.angcyo.core.component.file.writeTo
import com.angcyo.http.base.toJson
import com.angcyo.javafx.App
import com.angcyo.javafx.app
import com.angcyo.javafx.base.BaseController
import com.angcyo.javafx.base.ex.*
import com.angcyo.javafx.bean.AppConfigBean
import com.angcyo.javafx.bean.history
import com.angcyo.javafx.controller.MainController
import com.angcyo.javafx.http.IndustryHelper
import com.angcyo.javafx.ui.*
import com.angcyo.library.ex.toElapsedTime
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

        fun saveConfig(appConfigBean: AppConfigBean = app().appConfigBean) {
            appConfigBean.toJson {
                setPrettyPrinting()
            }.writeTo(File(CONFIG_PATH), false)
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
            dslOpenFile {
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

        //其他配置
        stage?.findByCss<CheckBox>("#topCheckBox")?.apply {
            isSelected = App.isAlwaysOnTopProperty.get()
            selectedProperty().addListener { observable, oldValue, newValue ->
                alwaysTop(newValue)
            }
        }

        initLink()
        initIndustry()
    }

    fun initLink() {
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
        initBin("#edgeBrowserBinLink", "https://www.microsoft.com/zh-cn/edge")
        initBin("#chromeBinLink", "https://chromedriver.storage.googleapis.com/index.html", false)
        initBin("#chromeBrowserBinLink", "https://www.google.cn/chrome/", false)
        initBin("#huohuBinLink", "https://github.com/mozilla/geckodriver/releases", false)
        initBin("#huohuBrowserBinLink", "http://www.firefox.com.cn/", false)
        initBin("#operaBinLink", "https://github.com/operasoftware/operachromiumdriver/releases", false)
        initBin("#operaBrowserBinLink", "https://www.opera.com/zh-cn", false)
        initBin("#safariBinLink", "https://github.com/operasoftware/operachromiumdriver/releases", false)
        initBin("#safariBrowserBinLink", "https://www.apple.com.cn/safari/", false)
        stage?.findByCss<ButtonBase>("#safariBinLink")?.apply {
            isDisable = true
            tooltip = Tooltip("内置")
        }
    }

    fun initIndustry() {
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
        cookieFieldNode?.text = app().appConfigBean.history().cookie
        cookieUpdateNode?.setOnAction {
            val cookie = cookieFieldNode?.text ?: ""
            app().appConfigBean.history().cookie = cookie
            saveConfig()

            switchToLoading(true)
            onBack {
                IndustryHelper.fetch(cookie)
                onLater {
                    dslAlert {
                        contentText = buildString {
                            appendln(
                                "更新成功,耗时[${
                                    IndustryHelper.duration().toElapsedTime(pattern = intArrayOf(-1, 1, 1))
                                }]:"
                            )
                            appendln("[内资]行业用语${IndustryHelper.nzIndustryFetch.allChildList.size}条;")
                            appendln("[内资秒批]行业用语${IndustryHelper.nzmpIndustryFetch.allChildList.size}条;")
                            appendln("[个体户]行业用语${IndustryHelper.gthIndustryFetch.allChildList.size}条;")
                            appendln("[个体户秒批]行业用语${IndustryHelper.gthmpIndustryFetch.allChildList.size}条;")
                        }
                    }
                    switchToLoading(false)
                }
            }
        }
    }

    /**置顶窗口*/
    fun alwaysTop(top: Boolean) {
        ctl<MainController>()?.topMenuItem?.isSelected = top
        stage?.findByCss<CheckBox>("#topCheckBox")?.isSelected = top
        App.isAlwaysOnTopProperty.set(top)
    }
}

fun updateConfig(action: AppConfigBean.() -> Unit) {
    app().appConfigBean.apply(action)
    TabConfigController.saveConfig()
}