package com.angcyo.javafx.controller.main

import com.angcyo.http.base.fromJson
import com.angcyo.http.base.toJson
import com.angcyo.javafx.base.BaseController
import com.angcyo.javafx.base.ex.*
import com.angcyo.javafx.controller.MainController
import com.angcyo.javafx.controller.showBottomTip
import com.angcyo.javafx.controller.showBottomTipAndAppendLog
import com.angcyo.javafx.item.DslTaskDebugItem
import com.angcyo.javafx.list.DslListItem
import com.angcyo.javafx.list.renderList
import com.angcyo.javafx.ui.*
import com.angcyo.javafx.web.TaskManager
import com.angcyo.library.LTime
import com.angcyo.library.ex.*
import com.angcyo.log.L
import com.angcyo.selenium.DslSelenium
import com.angcyo.selenium.LocatableWebElement
import com.angcyo.selenium.PairOutputType
import com.angcyo.selenium.auto.AutoControl
import com.angcyo.selenium.auto.action.Action
import com.angcyo.selenium.auto.action.ScreenshotAction
import com.angcyo.selenium.auto.controller.dslPutCode
import com.angcyo.selenium.bean.ActionBean
import com.angcyo.selenium.bean.CheckBean
import com.angcyo.selenium.bean.HandleBean
import com.angcyo.selenium.bean.TaskBean
import com.angcyo.selenium.js.exeJs
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.layout.Region
import javafx.scene.layout.StackPane
import javafx.stage.Stage
import javafx.stage.StageStyle
import org.openqa.selenium.Rectangle
import org.openqa.selenium.interactions.touch.TouchActions
import org.openqa.selenium.remote.RemoteWebDriver
import java.net.URL
import java.util.*

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/12/29
 */
class TabDebugController : BaseController() {

    /**启动按钮*/
    var startNode: Button? = null

    var taskTextNode: TextArea? = null

    /**Control切换*/
    val controlGroup = ToggleGroup()

    companion object {
        const val ACTION_PATH = "./json/action.json"
        const val TASK_PATH = "./json/task.json"
    }

    override fun initialize(stage: Stage?, location: URL?, resources: ResourceBundle?) {
        super.initialize(stage, location, resources)
        startNode = stage?.findByCss("#startNode")
        taskTextNode = stage?.findByCss("#taskTextNode")

        startNode?.setOnAction {
            if (DslSelenium.checkDriver()) {
                val taskBean = taskTextNode?.text?.fromJson<TaskBean>()
                if (taskBean == null) {
                    dslAlert {
                        alertType = Alert.AlertType.ERROR
                        contentText = "数据格式错误!"
                    }
                } else {
                    startNode?.isDisable = true
                    TaskManager.start(taskBean)
                }
            } else {
                dslAlert {
                    alertType = Alert.AlertType.ERROR
                    contentText = "无效的驱动程序!"
                }?.let {
                    if (it.get() == ButtonType.OK) {
                        ctl<MainController>()?.mainTabNode?.switchById("configTab")
                    }
                }
            }
        }
        startNode?.tooltip = Tooltip("启动任务")

        updateTaskText(TASK_PATH.readText() ?: getResourceAsStream("amr_task.json")?.bufferedReader()?.readText())
        taskTextNode?.saveOnTextChanged(TASK_PATH)

        //key save
        taskTextNode?.setOnKeyPressed {
            if (it.match(KeyCode.S, KeyCodeCombination.CONTROL_DOWN)) {
                //保存
                val text = taskTextNode?.text
                text?.fromJson<TaskBean>()?.let { taskBean ->
                    dslSaveFile {
                        title = "保存:${taskBean.title.or("")}"
                        extList.add(ext("Json文件", "*.json"))
                        extList.add(ext("所有文件", "*.*"))
                    }?.let {
                        it.writeText(text)
                    }
                }
            }
        }

        //init
        _initTestNode(stage)
        _initTestDriver(stage)
        _initDebugListView(stage)

        //test
        ScreenshotAction.screenshotAction = { screenshot, clipImage1, clipImage2 ->
            onMain {
                showScreenshot(stage, screenshot, clipImage1, clipImage2)
            }
        }
    }

    /**激活开始按钮*/
    fun enableStartNode(enable: Boolean) {
        onMain {
            startNode?.isDisable = !enable
        }
    }

    fun updateTaskText(string: String?) {
        taskTextNode?.text = string
    }

    //测试界面
    fun _initTestNode(stage: Stage?) {
        //control
        stage?.find<RadioButton>("#testControlRadioButton")?.let {
            it.isSelected = true
            it.toggleGroup = controlGroup
        }
        stage?.find<RadioButton>("#taskControlRadioButton")?.let {
            it.toggleGroup = controlGroup
        }

        val startActionNode: Button? = stage?.findByCss("#startActionNode")
        val actionAreaNode: TextArea? = stage?.findByCss("#actionAreaNode")

        //丢失焦点之后, 格式化json
        actionAreaNode?.focusedProperty()?.addListener { observable, oldValue, newValue ->
            if (!newValue) {
                actionAreaNode.text?.fromJson<ActionBean>()?.apply {
                    actionAreaNode.text = this.toJson {
                        setPrettyPrinting()
                    }
                }
            }
        }

        startActionNode?.setOnAction {
            //插入一个指定的ActionBean, 并执行
            val actionJson = actionAreaNode?.text
            val actionBean: ActionBean? = actionJson?.fromJson()
            actionBean?.also {
                TaskManager._currentControl?.apply {
                    actionRunSchedule.startNextAction(it)
                }.elseNull {
                    showBottomTip("请先[启动任务]!")
                }
            }.elseNull {
                showBottomTip("数据格式不合法!")
            }
        }

        actionAreaNode?.text = ACTION_PATH.readText()
        actionAreaNode?.saveOnTextChanged(ACTION_PATH)

        //key save
        actionAreaNode?.setOnKeyPressed {
            if (it.match(KeyCode.S, KeyCodeCombination.CONTROL_DOWN)) {
                //保存
                val text = actionAreaNode.text
                text?.fromJson<ActionBean>()?.let { actionBean ->
                    dslSaveFile {
                        title = "保存:${actionBean.title.or("")}"
                        extList.add(ext("Json文件", "*.json"))
                        extList.add(ext("所有文件", "*.*"))
                    }?.let {
                        it.writeText(text)
                    }
                }
            }
        }
    }

    //测试驱动
    var testControl: AutoControl? = null
    fun _initTestDriver(stage: Stage?) {
        val connectDriverNode: Region? = stage?.findByCss("#connectDriverNode")
        val connectDriverProgressBar: Region? = stage?.findByCss("#connectDriverProgressBar")

        fun connectLoading(loading: Boolean) {
            connectDriverNode.enable(!loading)
            connectDriverProgressBar?.visible(false)
        }

        //连接驱动
        connectLoading(false)
        connectDriverNode?.setOnMouseClicked {
            connectLoading(true)

            val taskBean = TaskBean().apply {
                title = "驱动测试任务"
                des = nowTimeString()
            }

            //connect
            if (isSelectTaskControl()) {
                TaskManager.start(taskBean)
            } else {
                testControl = AutoControl().apply {
                    logAction = {
                        L.wt(it)
                        appendLog(it)
                        showBottomTip(it)
                    }
                    driverProperty.addListener { observable, oldValue, newValue ->
                        connectLoading(false)
                    }
                    start(taskBean)
                }
            }
        }

        //打开amr
        val amrUrl = "https://amr.sz.gov.cn/aicmerout/jsp/gcloud/giapout/industry/aicmer/processpage/step_prewin.jsp"
        stage?.findByCss<Node>("#openAmrButton")?.setOnMouseClicked {
            _checkTestConnect {
                actionRunSchedule.startNextAction(ActionBean(check = CheckBean().apply {
                    handle = listOf(HandleBean(actionList = listOf("${Action.ACTION_TO}:$amrUrl")))
                }))
            }
        }

        //打开任意url
        var customUrl: String? = amrUrl
        stage?.findByCss<Node>("#openUrlButton")?.setOnMouseClicked {
            val url = dslInput {
                textInputDefaultValue = customUrl
                title = "请输入需要跳转的网址"
                configInputDialog = {
                    it.dialogPane.prefWidth = 500.0
                }
            }.getOrNull()
            if (!url.isNullOrEmpty()) {
                customUrl = url
                _checkTestConnect {
                    actionRunSchedule.startNextAction(ActionBean(check = CheckBean().apply {
                        handle = listOf(HandleBean(actionList = listOf("${Action.ACTION_TO}:$customUrl")))
                    }))
                }
            }
        }

        //截图
        val screenshotPane = stage?.findByCss<Node>("#screenshotPane")
        val screenshotTipNode = stage?.findByCss<Label>("#screenshotTipNode")
        screenshotPane?.visible(false)
        stage?.findByCss<Node>("#screenshotButton")?.setOnMouseClicked {
            LTime.tick()
            _checkTestConnect {
                (driver as? RemoteWebDriver)?.getScreenshotAs(PairOutputType())?.let { pair ->
                    screenshotPane?.visible(true)
                    val image = pair.second
                    screenshotTipNode?.text = "${LTime.time()} ${image.width}×${image.height}"
                    val clipImage = image.clipRect(100, 100, 100, 100)
                    showScreenshot(stage, image)
                }
            }
        }

        //run action
        val runActionNode: Button? = stage?.findByCss("#runActionNode")
        val actionAreaNode: TextArea? = stage?.findByCss("#actionAreaNode")
        runActionNode?.setOnAction {
            //插入一个指定的ActionBean, 并执行
            val actionJson = actionAreaNode?.text
            val actionBean: ActionBean? = actionJson?.fromJson()
            actionBean?.let {
                _runTestAction(it)
            }.elseNull {
                showBottomTip("数据格式不合法!")
            }
        }

        //pause run
        stage?.find<Node>("#pauseActionNode")?.setOnMouseClicked {
            testControl?.apply {
                actionRunSchedule.clearTempAction()
                pause()
            }
            TaskManager._currentControl?.apply {
                actionRunSchedule.clearTempAction()
                pause()
            }
        }

        //resume run
        stage?.find<Node>("#resumeActionNode")?.setOnMouseClicked {
            testControl?.apply {
                resume()
            }
            TaskManager._currentControl?.apply {
                pause()
            }
        }

        //refresh
        stage?.find<Node>("#refreshActionNode")?.setOnMouseClicked {
            _checkTestConnect {
                onBack {
                    (driver as? RemoteWebDriver)?.navigate()?.apply {
                        refresh()
                        showBottomTip("刷新完成!")
                    }
                }
            }
        }

        //手势测试
        stage?.find<Node>("#gestureActionNode")?.setOnMouseClicked {
            _checkTestConnect {
                driver?.let {
                    //Actions(it).moveByOffset(200, 200).perform()
                    TouchActions(it).apply {
                        //scroll(200, 200)
                        //flick(200, 200)
                        //down(95, 300)
                        //up(95, 300)
                        click(LocatableWebElement(Rectangle(95, 300, 10, 10)))
                    }.perform()
                }
            }
        }

        //test
        stage?.find<Node>("#testActionNode")?.setOnMouseClicked {
            _checkTestConnect {
                val exeJsResult = (driver as? RemoteWebDriver)?.exeJs("get_element_bounds.js", "button[tabindex='0']")
                showBottomTipAndAppendLog("执行js返回:${exeJsResult}")
            }

            val base64 =
                "iVBORw0KGgoAAAANSUhEUgAAALQAAABGCAMAAABSSU4SAAAAP1BMVEUAAAAGQU00b3svanZXkp5NiJR4s78nYm5emaVGgY01cHwvanZXkp4gW2cFQEwAO0djnqqm4e1Uj5uY098zbnrFG7VfAAAAAXRSTlMAQObYZgAAA75JREFUeJzsmo2OmzAQhL3JXX6kRBeR93/X6gDbsz8G29iEqt1IVUvAfB6Pdxcad8B4vV55J16v194sufF6ZVJfr12oH49H+UVZ0EM36MejhjoHehiGY0GPnv7+/l4851fpPp6uhHbul3mFul9kM9P88VEP/Xw+q64rD/LhD1RDP5/7UBMwI3XVYPtAAzLTujKaQf+SpGhmffHD43K5FN2sDfOENUNPUAFspCQ4UUl9uZRSbw+5+MIFnDIh9N7QAlNSk8D8uNKzJxAT/pwShWA096HJvHm/JqE9n/ewZRHJnAvTgzrqCVhaewGpHZ0cf3tuNMcUK+/nIQUXF+XfwDXlpklSrlqwsbIJuzD/JnqdtiBb6y7ThNifcGXpjRoyO6WZyhMuOsXNW7JE6YbQTGTijOpEEJdKZa6iNkWBUTgFyQwFk8AMXhpl2ESyljFnTH+5OyjW1gB+RhuWuujCUN38x+cgChO63+/erYbSRrbrTU2RD5JA3E9Bad9ikHErzVyDnU0diYHcsUKIJVFtABKHeAYsyiFF1EpqJ6xOwSiq5vqvYDAHfXepT3K9pZQNWY5IwaniyJUW1YVmNxXWmYwkH9edzVJNmC8EnMTniZmECFIg90qaKk9s5Qwb2qaW6lqWd7C7ab36nE6nVbXJQrbG1Q4JDqDEPPUzmp9jGvp0GqkNAcxB5XF7eh41CBd2a1o/YF5VeoTmrWRiMNvBCWafvx1kiunwLUXi4rVZSseHpThNh1kusazJeRBYNGSM8Z+3W5o6P0Zm38qLhp09LdleEAfgkSCmutiXLipdF8lizSqfaO7xeug9MU9Alm9M7MQ2Zh27WIE4SflKAMhYOanoRoci8llb43lfOoWDqOLj3nhtXCP7JZ5kHoqoU5PR6ZpZHZzrmd9vPl/H7bdC3YDZgzGdWEbWzfTbSWjCrnzP4FrL0mNWSad72ITS5/O5EzX4QlpGwUBnmKH0+dyNWnT8uDs1NJF4ilCj3faB5lKp/ogxO/CyGVgku0KnXxDp3G28NeOBRbIrc6LLZI0cqafDj4ctdUh9rMs7TlgiYhJp/Xq2SVhSs//XWq96neJr4TuDiufrD8n89bVAnWAOSk/Hfipuu3GJlpjT/TTEz085dUdj8QYiWUEqlG4JLV/JGSm5zb2aKk3yVQ2xdzDtbtXUHaQ+cJujVb85SEf45pDAY+in9fjNscOmPnpYnv4no3Mf3SV6PLFk/lS4PjpAZ//AuToWoSt/HtRd6SVP7/7rsSbxV0L/j93iTwAAAP//7cUO6Bf0SxQAAAAASUVORK5CYII="
            val file = "./screenshot/2021-01-14_17-39-54-503_imageCode.png"
            showBottomTip(dslPutCode(base64))
        }

        //url
        stage?.findByCss<Node>("#openAmrLink")?.apply {
            setOnMouseClicked {
                //openUrl(amrUrl)
                showDocument(amrUrl)
            }
        }
    }

    /**显示截图*/
    fun showScreenshot(stage: Stage?, image: Image? = null, image2: Image? = null, image3: Image? = null) {
        val screenshotPane = stage?.findByCss<Node>("#screenshotPane")
        val screenshotImageView = stage?.findByCss<ImageView>("#screenshotImageView")
        val screenshotImageView1 = stage?.findByCss<ImageView>("#screenshotImageView1")
        val screenshotImageView2 = stage?.findByCss<ImageView>("#screenshotImageView2")

        screenshotPane?.visible(true)

        screenshotImageView?.image = image
        screenshotImageView1?.image = image2
        screenshotImageView2?.image = image3

        //双击查看大图
        screenshotImageView?.setOnMouseDoubleClicked {
            L.i("show...")
            showImagePreview(image)
        }
        screenshotImageView1?.setOnMouseDoubleClicked {
            showImagePreview(image2)
        }
        screenshotImageView2?.setOnMouseDoubleClicked {
            showImagePreview(image3)
        }
        //showDocument(pair.first)
    }

    fun isSelectTaskControl() = (controlGroup.selectedToggle as? Node)?.id == "taskControlRadioButton"

    /**获取控制器*/
    fun getSelectControl() = if (isSelectTaskControl()) {
        TaskManager._currentControl
    } else {
        testControl
    }

    //check
    fun _checkTestConnect(action: AutoControl.() -> Unit) {
        getSelectControl()?.let {
            it.action()
        }.elseNull {
            val control = if (isSelectTaskControl()) "task" else "test"
            showBottomTip("请先[连接驱动][${control}]!")
        }
    }

    //run action
    fun _runTestAction(actionBean: ActionBean, fromTask: TaskBean? = null) {
        _checkTestConnect {
            fromTask?.apply {
                _currentTaskBean?.wordList = wordList
                _currentTaskBean?.textMap = textMap
            }
            actionBean.enable = true
            //actionBean.conditionList
            actionRunSchedule.startNextAction(actionBean)
        }
    }

    fun showImagePreview(image: Image?) {
        val stage = Stage()
        stage.initStyle(StageStyle.UTILITY)
        stage.scene = Scene(StackPane().apply {
            alignment = Pos.CENTER
            children.add(ImageView().apply {
                setImage(image)
            })
        })
        stage.show()
    }

    fun _initDebugListView(stage: Stage?) {
        val debugListView = stage?.findByCss<ListView<DslListItem>>("#debugListView")
        debugListView?.renderList {
            val taskList = TaskManager.debugTaskList.toMutableList()
            taskList.add(TaskBean().apply {
                title = "回退弹窗处理"
                actionList = TaskManager.backActionList
            })

            //load list
            taskList.forEach { bean ->
                DslTaskDebugItem()() {
                    taskBean = bean
                    clickAction = {
                        showToCustomActionPane(stage, it)
                    }
                    doubleAction = {
                        _runTestAction(it, taskBean)
                    }
                    clickTaskAction = {
                        showToCustomTaskPane(stage, it)
                    }
                    startTaskAction = {
                        _checkTestConnect {
                            restart(it)
                        }
                    }
                }
            }
        }
    }

    /**展开自定义的ActionBean Pane*/
    fun showToCustomActionPane(stage: Stage?, actionBean: ActionBean) {
        val titledPane = stage?.find<TitledPane>("customActionPane")
        val actionArea = stage?.find<TextArea>("actionAreaNode")
        titledPane?.isExpanded = true
        actionArea?.text = actionBean.toJson {
            setPrettyPrinting()
        }
    }

    /**展开自定义的TaskBean Pane*/
    fun showToCustomTaskPane(stage: Stage?, taskBean: TaskBean) {
        val actionTitledPane = stage?.find<TitledPane>("customActionPane")
        actionTitledPane?.isExpanded = false

        val titledPane = stage?.find<TitledPane>("customTaskPane")
        titledPane?.isExpanded = true

        val taskTextNode = stage?.find<TextArea>("taskTextNode")
        taskTextNode?.text = taskBean.toJson {
            setPrettyPrinting()
        }
    }
}