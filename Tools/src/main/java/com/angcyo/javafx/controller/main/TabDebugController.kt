package com.angcyo.javafx.controller.main

import com.angcyo.http.base.fromJson
import com.angcyo.http.base.toJson
import com.angcyo.javafx.base.BaseController
import com.angcyo.javafx.base.ex.ctl
import com.angcyo.javafx.base.ex.findByCss
import com.angcyo.javafx.base.ex.onMain
import com.angcyo.javafx.base.ex.showDocument
import com.angcyo.javafx.controller.MainController
import com.angcyo.javafx.list.DslListItem
import com.angcyo.javafx.list.renderList
import com.angcyo.javafx.ui.*
import com.angcyo.javafx.web.Task
import com.angcyo.library.LTime
import com.angcyo.library.ex.*
import com.angcyo.log.L
import com.angcyo.selenium.DslSelenium
import com.angcyo.selenium.PairOutputType
import com.angcyo.selenium.auto.AutoControl
import com.angcyo.selenium.auto.action.Action
import com.angcyo.selenium.bean.ActionBean
import com.angcyo.selenium.bean.CheckBean
import com.angcyo.selenium.bean.HandleBean
import com.angcyo.selenium.bean.TaskBean
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.Region
import javafx.scene.layout.Region.USE_COMPUTED_SIZE
import javafx.scene.layout.StackPane
import javafx.stage.Stage
import javafx.stage.StageStyle
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
                    Task.start(taskBean)
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

        //init
        _initTestNode(stage)
        _initTestDriver(stage)
        _initDebugListView(stage)
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
                Task._currentControl?.apply {
                    actionRunSchedule.startNextAction(it)
                }.elseNull {
                    ctl<MainController>()?.bottomTip("请先[启动任务]!")
                }
            }.elseNull {
                ctl<MainController>()?.bottomTip("数据格式不合法!")
            }
        }

        actionAreaNode?.text = ACTION_PATH.readText()
        actionAreaNode?.saveOnTextChanged(ACTION_PATH)
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
            testControl = AutoControl().apply {
                logAction = {
                    L.wt(it)
                    ctl<TabLogController>()?.appendLog(it)
                }
                driverProperty.addListener { observable, oldValue, newValue ->
                    connectLoading(false)
                }
                start(TaskBean().apply {
                    title = "驱动测试任务"
                    des = nowTimeString()
                })
            }
        }

        //check
        fun checkConnect(action: AutoControl.() -> Unit) {
            testControl?.let {
                it.action()
            }.elseNull {
                ctl<MainController>()?.bottomTip("请先[连接驱动]!")
            }
        }

        //打开amr
        val amrUrl = "https://amr.sz.gov.cn/aicmerout/jsp/gcloud/giapout/industry/aicmer/processpage/step_prewin.jsp"
        stage?.findByCss<Node>("#openAmrButton")?.setOnMouseClicked {
            checkConnect {
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
                checkConnect {
                    actionRunSchedule.startNextAction(ActionBean(check = CheckBean().apply {
                        handle = listOf(HandleBean(actionList = listOf("${Action.ACTION_TO}:$customUrl")))
                    }))
                }
            }
        }

        //截图
        val screenshotPane = stage?.findByCss<Node>("#screenshotPane")
        val screenshotImageView = stage?.findByCss<ImageView>("#screenshotImageView")
        val screenshotImageView1 = stage?.findByCss<ImageView>("#screenshotImageView1")
        val screenshotImageView2 = stage?.findByCss<ImageView>("#screenshotImageView2")
        val screenshotTipNode = stage?.findByCss<Label>("#screenshotTipNode")
        screenshotPane?.visible(false)
        stage?.findByCss<Node>("#screenshotButton")?.setOnMouseClicked {
            LTime.tick()
            checkConnect {
                (driver as? RemoteWebDriver)?.getScreenshotAs(PairOutputType())?.let { pair ->
                    screenshotPane?.visible(true)
                    val image = pair.second
                    screenshotImageView?.image = image
                    screenshotTipNode?.text = "${LTime.time()} ${image.width}×${image.height}"

                    val clipImage = image.clipRect(100, 100, 100, 100)
                    //screenshotImageView1?.image = clipImage
                    //screenshotImageView2?.image = clipImage.toByteArray().toImage(50, 50)

                    //双击查看大图
                    screenshotImageView?.setOnMouseDoubleClicked {
                        showImagePreview(image)
                    }

                    //showDocument(pair.first)
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
                checkConnect {
                    actionRunSchedule.startNextAction(it)
                }.elseNull {
                    ctl<MainController>()?.bottomTip("数据格式不合法!")
                }
            }
        }

        //url
        stage?.findByCss<Node>("#openAmrLink")?.apply {
            setOnMouseClicked {
                //openUrl(amrUrl)
                showDocument(amrUrl)
            }
        }
    }

    fun showImagePreview(image: Image) {
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
            DslListItem()() {
                bindItem = {
                    Button().apply {
                        text = nowTimeString()
                    }
                }
            }
            DslListItem()() {
                bindItem = {
                    TitledPane().apply {
                        val pane = this
                        text = nowTimeString()
                        prefHeight = 300.0
                        content = Button().apply {
                            text = nowTimeString()
                            setOnAction {
                                L.i("requestLayout")
                                pane.prefHeight = if (pane.prefHeight == USE_COMPUTED_SIZE) 100.0 else USE_COMPUTED_SIZE
                            }
                        }
                    }
                }
            }
            DslListItem()() {
                bindItem = {
                    Button().apply {
                        text = nowTimeString()
                    }
                }
            }
        }
    }
}