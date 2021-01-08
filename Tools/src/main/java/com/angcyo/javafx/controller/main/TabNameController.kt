package com.angcyo.javafx.controller.main

import com.angcyo.http.post
import com.angcyo.http.rx.observe
import com.angcyo.http.toBean
import com.angcyo.javafx.annotation.NodeInject
import com.angcyo.javafx.app
import com.angcyo.javafx.base.BaseController
import com.angcyo.javafx.base.OSInfo
import com.angcyo.javafx.base.ex.find
import com.angcyo.javafx.base.ex.findByCss
import com.angcyo.javafx.base.ex.onLater
import com.angcyo.javafx.bean.CompanyWordBean
import com.angcyo.javafx.bean.NameTaskBean
import com.angcyo.javafx.bean.history
import com.angcyo.javafx.controller.showBottomTip
import com.angcyo.javafx.http.HttpHelper
import com.angcyo.javafx.item.DslNameTaskItem
import com.angcyo.javafx.list.DslListItem
import com.angcyo.javafx.list.renderList
import com.angcyo.javafx.ui.*
import com.angcyo.javafx.web.Task
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

    @NodeInject
    var nameTaskListView: ListView<DslListItem>? = null

    //企业类型
    val typeToggleGroup = ToggleGroup()

    //内资企业类型
    val nzTypeToggleGroup = ToggleGroup()

    override fun initialize(stage: Stage?, location: URL?, resources: ResourceBundle?) {
        super.initialize(stage, location, resources)

        accountNameNode?.text = app().appConfigBean.history?.username
        accountPwNode?.text = app().appConfigBean.history?.password

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
                        fixedCellSize = if (OSInfo.isMac) 30.0 else 20.0
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

        fun updateTaskList() {
            nameTaskListView?.renderList {
                Task.nameTaskList.forEach {
                    DslNameTaskItem()() {
                        nameTaskBean = it
                    }
                }
            }
        }

        updateTaskList()

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
                val bean = NameTaskBean().apply {
                    this.username = username
                    this.password = password
                    this.companyWord = companyWord
                    this.termsWord = termsWord
                    this.companyTypeName = when (companyTypeNodeId) {
                        "entValueNode1" -> "内资"
                        "entValueNode2" -> "外资"
                        "entValueNode3" -> "个体户"
                        "entValueNode4" -> "集团"
                        "entValueNode6" -> "深圳市内资有限公司全流程设立（秒批）"
                        "entValueNode5" -> "深圳市个体户全流程设立（秒批）"
                        else -> null
                    }
                    this.companyType = when (companyTypeNodeId) {
                        //内资
                        "entValueNode1" -> "input[name='entType1'][value='1']"
                        //外资
                        "entValueNode2" -> "input[name='entType1'][value='2']"
                        //个体户
                        "entValueNode3" -> "input[name='entType1'][value='3']"
                        //集团
                        "entValueNode4" -> "input[name='entType1'][value='4']"
                        //深圳市内资有限公司全流程设立（秒批）
                        "entValueNode6" -> "input[name='entType1'][value='6']"
                        //深圳市个体户全流程设立（秒批）
                        "entValueNode5" -> "input[name='entType1'][value='5']"
                        else -> null
                    }
                    if (companyTypeNodeId == "entValueNode1") {
                        //内资公司类型
                        this.nzCompanyTypeName = when (nzCompanyTypeNodeId) {
                            "nzValueGSNode" -> "公司"
                            "nzValueFGSNode" -> "分公司"
                            "nzValueGRDZNode" -> "个人独资"
                            "nzValueGRDZFZNode" -> "个人独资分支"
                            "nzValueHHQYNode" -> "合伙企业"
                            "nzValueHHFZNode" -> "合伙分支"
                            "nzValueOTHERNZNode" -> "其他"
                            else -> null
                        }
                        this.nzCompanyType = when (nzCompanyTypeNodeId) {
                            //公司
                            "nzValueGSNode" -> "input[name='nzType1'][value='GS']"
                            //分公司
                            "nzValueFGSNode" -> "input[name='nzType1'][value='FGS']"
                            //个人独资
                            "nzValueGRDZNode" -> "input[name='nzType1'][value='GRDZ']"
                            //个人独资分支
                            "nzValueGRDZFZNode" -> "input[name='nzType1'][value='GRDZFZ']"
                            //合伙企业
                            "nzValueHHQYNode" -> "input[name='nzType1'][value='HHQY']"
                            //合伙分支
                            "nzValueHHFZNode" -> "input[name='nzType1'][value='HHFZ']"
                            //其他
                            "nzValueOTHERNZNode" -> "input[name='nzType1'][value='OTHERNZ']"
                            else -> null
                        }
                    }
                }

                //保存历史记录
                app().appConfigBean.history().apply {
                    (termsWordList?.toMutableList() ?: mutableListOf()).apply {
                        remove(termsWord)
                        add(0, termsWord ?: "")
                        termsWordList = this
                    }
                    this.username = username
                    this.password = password
                }
                TabConfigController.saveConfig()
                Task.addNameTaskBean(bean)

                nameTaskListView?.renderList(false) {
                    itemsList.add(0, DslNameTaskItem().apply {
                        nameTaskBean = bean
                    })
                    updateList()
                }
            }
        }
    }
}