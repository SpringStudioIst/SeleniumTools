package com.angcyo.javafx.controller.main

import com.angcyo.http.post
import com.angcyo.http.rx.observe
import com.angcyo.http.toBean
import com.angcyo.javafx.annotation.NodeInject
import com.angcyo.javafx.app
import com.angcyo.javafx.base.BaseController
import com.angcyo.javafx.base.ex.find
import com.angcyo.javafx.base.ex.findByCss
import com.angcyo.javafx.base.ex.onLater
import com.angcyo.javafx.bean.CompanyWordBean
import com.angcyo.javafx.bean.history
import com.angcyo.javafx.controller.showBottomTip
import com.angcyo.javafx.http.HttpHelper
import com.angcyo.javafx.ui.*
import com.angcyo.library.ex.orString
import com.angcyo.log.L
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.layout.Pane
import javafx.scene.layout.Region
import javafx.stage.Stage
import java.net.URL
import java.util.*

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/01/04
 */
class TabNameController : BaseController() {

    /**帐号*/
    @NodeInject
    var accountNameNode: TextField? = null

    /**密码*/
    @NodeInject
    var accountPwNode: TextField? = null

    /**字号*/
    @NodeInject
    var companyWordTextNode: TextField? = null

    /**行业用语*/
    @NodeInject
    var termsWordComboBox: ComboBox<String>? = null
    
    //企业类型
    val typeToggleGroup = ToggleGroup()

    //内资企业类型
    val nzTypeToggleGroup = ToggleGroup()

    companion object {
        val companyTypeMap = hashMapOf(
            "entValueNode1" to "entValueNode1",
            "entValueNode2" to "entValueNode1",
            "entValueNode3" to "entValueNode1",
            "entValueNode4" to "entValueNode1",
            "entValueNode5" to "entValueNode1",
            "entValueNode6" to "entValueNode1",
        )
        val nzCompanyTypeMap = hashMapOf(
            "nzValueGSNode" to "entValueNode1",
            "nzValueFGSNode" to "entValueNode1",
            "nzValueGRDZNode" to "entValueNode1",
            "nzValueGRDZFZNode" to "entValueNode1",
            "nzValueHHQYNode" to "entValueNode1",
            "nzValueHHFZNode" to "entValueNode1",
            "nzValueOTHERNZNode" to "entValueNode1",
        )
    }

    override fun initialize(stage: Stage?, location: URL?, resources: ResourceBundle?) {
        super.initialize(stage, location, resources)
        stage?.let {
            initCompanyType(it)
            initCompanyWord(it)
            initTradeTerms(it)
            initCreateTask(it)
        }
    }

    /**企业类型, 内资企业类型*/
    fun initCompanyType(stage: Stage) {
        val companyTypeFlowPane = stage.findByCss<Pane>("#companyTypeFlowPane")
        val companyNzTypePane = stage.findByCss<TitledPane>("#companyNzTypePane")
        val companyNzTypeFlowPane = stage.findByCss<Pane>("#companyNzTypeFlowPane")

        //企业类别
        typeToggleGroup.selectedToggleProperty().addListener { observable, oldValue, newValue ->
            if (newValue is RadioButton) {
                L.i(newValue.id)
                //companyNzTypeFlowPane?.parent?.isDisable = newValue.id != "entValueNode1"
                //stage.findByCss<Node>("#companyNzTypePane")?.isDisable = newValue.id != "entValueNode1"
                //companyNzTypeFlowPane?.isDisable = newValue.id != "entValueNode1"
                //companyNzTypeFlowPane?.visible(newValue.id == "entValueNode1")

                var oldHeight = Region.USE_COMPUTED_SIZE
                companyNzTypePane?.also { pane ->
                    if (newValue.id != "entValueNode1") {
                        pane.minHeight = 0.0
                        oldHeight = pane.height
                        dslTimeline {
                            addKeyFrame(0.0, pane.prefHeightProperty(), pane.height)
                            addKeyFrame(240.0, pane.prefHeightProperty(), 0)
                            setOnFinished {
                                pane.visible(false)
                            }
                        }
                    } else {
                        pane.visible(true)
                        dslTimeline {
                            addKeyFrame(240.0, pane.prefHeightProperty(), oldHeight)
                            setOnFinished {
                                pane.minHeight = Region.USE_COMPUTED_SIZE
                                pane.prefHeight = Region.USE_COMPUTED_SIZE
                            }
                        }
                    }
                }
            }
        }
        companyTypeFlowPane?.children?.forEach {
            if (it is RadioButton) {
                it.toggleGroup = typeToggleGroup
            }
        }

        //内资企业类别
        nzTypeToggleGroup.selectedToggleProperty().addListener { observable, oldValue, newValue ->
            if (newValue is RadioButton) {
                L.i(newValue.id)
            }
        }

        companyNzTypeFlowPane?.children?.forEach {
            if (it is RadioButton) {
                it.toggleGroup = nzTypeToggleGroup
            }
        }
    }

    /**字号*/
    fun initCompanyWord(stage: Stage) {
        //字号
        val companyWordListView: ListView<String>? = stage.find("companyWordListView")

        //城市名
        val cityComboBox: ComboBox<String>? = stage.find("cityComboBox")
        //行业名
        val industryComboBox: ComboBox<String>? = stage.find("industryComboBox")
        //获取
        val getWordSuggestButton: Button? = stage.find("getWordSuggestButton")

        //启用和禁用 获取按钮
        fun updateGetWordButton() {
            getWordSuggestButton.enable(
                !cityComboBox?.editor?.text.isNullOrEmpty() &&
                        !industryComboBox?.editor?.text.isNullOrEmpty()
            )
        }

        fun updateCityComboBox() {
            cityComboBox?.resetItemList(
                app().appConfigBean.history().cityList,
                app().appConfigBean.history().cityList?.firstOrNull()
            )
        }

        fun addCityHistory(value: String?) {
            if (!value.isNullOrEmpty()) {
                (app().appConfigBean.history().cityList?.toMutableList() ?: mutableListOf()).apply {
                    remove(value)
                    add(0, value)
                    app().appConfigBean.history().cityList = this
                }
            }
        }

        fun updateIndustryComboBox() {
            industryComboBox?.resetItemList(
                app().appConfigBean.history().industryList,
                app().appConfigBean.history().industryList?.firstOrNull()
            )
        }

        fun addIndustryHistory(value: String?) {
            if (!value.isNullOrEmpty()) {
                (app().appConfigBean.history().industryList?.toMutableList() ?: mutableListOf()).apply {
                    remove(value)
                    add(0, value)
                    app().appConfigBean.history().industryList = this
                }
            }
        }

        updateCityComboBox()
        updateIndustryComboBox()
        updateGetWordButton()

        //监听输入改变
        cityComboBox?.editor.onTextChanged { observable, oldValue, newValue ->
            updateGetWordButton()
        }
        industryComboBox?.editor.onTextChanged { observable, oldValue, newValue ->
            updateGetWordButton()
        }

        //获取按钮
        companyWordListView.visible(false)
        var wordResult: CompanyWordBean? = null
        getWordSuggestButton?.setOnAction {
            getWordSuggestButton.enable(false)
            val city = cityComboBox?.value.orString()
            val industry = industryComboBox?.value.orString()
            showBottomTip("加载中...[$city] [$industry]")
            post {
                url = "http://www.qimingbao365.cn/service/setNameCon/getRandomName"
                formMap =
                    hashMapOf("cityName" to city, "hangyeName" to industry)
                isSuccessful = {
                    it.isSuccessful
                }
            }.observe { data, error ->
                getWordSuggestButton.enable(true)
                val bean: CompanyWordBean? = data?.toBean()
                wordResult = bean
                bean?.also {
                    //追加到历史
                    addCityHistory(city)
                    addIndustryHistory(industry)
                    updateCityComboBox()
                    updateIndustryComboBox()
                    TabConfigController.saveConfig()
                    //更新列表ui
                    companyWordListView?.apply {
                        visible(true)
                        fixedCellSize = 20.0
                        val mapList = it.result?.mapIndexedTo(mutableListOf()) { index, s ->
                            "$s    L${it.level?.getOrNull(index)}"
                        }
                        resetItemList(mapList)
                        prefHeight = (fixedCellSize - 2.5) * ((items?.size ?: 0) + 1)
                    }
                }
                error?.also {
                    showBottomTip(it.message)
                    alertError(it.message)
                }
            }
        }

        //列表选择
        companyWordListView?.selectionModel?.selectedIndexProperty()
            ?.addListener { observable, oldValue, newValue ->
                companyWordTextNode?.text = wordResult?.result?.getOrNull(newValue as Int)
            }
    }

    /**行业用语*/
    fun initTradeTerms(stage: Stage) {
        //行业用语
        val termsWordListView: ListView<String>? = stage.find("termsWordListView")

        //更新列表
        fun filterTermsWordList(word: String?) {
            val all = HttpHelper.industryList.filter { !it.name.isNullOrEmpty() }.mapTo(mutableListOf()) {
                //"${it.id} ${it.name}"
                "${it.name}"
            }
            if (word.isNullOrEmpty()) {
                termsWordListView?.resetItemList(all)
            } else {
                termsWordListView?.resetItemList(all.filter {
                    it.contains(word)
                }, null)
            }
        }

        //默认
        filterTermsWordList("")

        //监听输入改变
        termsWordComboBox?.setOnKeyReleased {
            filterTermsWordList(termsWordComboBox?.editor?.text)
        }

        //输入历史
        termsWordComboBox?.resetItemList(app().appConfigBean.history().termsWordList, null)

        //列表选中
        termsWordListView?.onSelected { selectedIndex, selectedItem ->
            onLater {
                termsWordComboBox?.value = selectedItem
            }
        }
    }

    fun initCreateTask(stage: Stage) {
        val createTaskButton: Button? = stage.find("createTaskButton")

        fun checkEmpty(text: String?, message: String): Boolean {
            if (text.isNullOrEmpty()) {
                showBottomTip(message)
                return true
            }
            return false
        }

        createTaskButton?.setOnAction {
            val username = accountNameNode?.text
            val password = accountPwNode?.text
            val companyTypeNodeId = (typeToggleGroup.selectedToggle as? Node)?.id
            val nzCompanyTypeNodeId = (nzTypeToggleGroup.selectedToggle as? Node)?.id
            val companyWord = companyWordTextNode?.text
            val termsWord = termsWordComboBox?.value
            if (checkEmpty(username, "请输入账号") ||
                checkEmpty(password, "请输入密码") ||
                checkEmpty(companyWord, "请输入字号") ||
                checkEmpty(termsWord, "请输入行业用语")
            ) {

            } else {

            }
        }
    }
}