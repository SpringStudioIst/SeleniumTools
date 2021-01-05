package com.angcyo.javafx.controller.main

import com.angcyo.javafx.base.BaseController
import com.angcyo.javafx.base.ex.findByCss
import com.angcyo.javafx.ui.visible
import com.angcyo.log.L
import javafx.scene.control.RadioButton
import javafx.scene.control.ToggleGroup
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
    override fun initialize(stage: Stage?, location: URL?, resources: ResourceBundle?) {
        super.initialize(stage, location, resources)
        stage?.let {
            initCompanyType(it)
        }
    }

    fun initCompanyType(stage: Stage) {
        val companyTypeFlowPane = stage.findByCss<Pane>("#companyTypeFlowPane")
        val companyNzTypeFlowPane = stage.findByCss<Pane>("#companyNzTypeFlowPane")

        //企业类别
        val typeToggleGroup = ToggleGroup().apply {
            selectedToggleProperty().addListener { observable, oldValue, newValue ->
                if (newValue is RadioButton) {
                    L.i(newValue.id)
                    //companyNzTypeFlowPane?.parent?.isDisable = newValue.id != "entValueNode1"
                    //stage.findByCss<Node>("#companyNzTypePane")?.isDisable = newValue.id != "entValueNode1"
                    companyNzTypeFlowPane?.isDisable = newValue.id != "entValueNode1"
                    stage.findByCss<Region>("#companyNzTypePane")?.visible(newValue.id == "entValueNode1")

                    if (newValue.id != "entValueNode1") {
                        companyNzTypeFlowPane?.maxHeight = 0.0

                    } else {
                        companyNzTypeFlowPane?.maxHeight = Region.USE_COMPUTED_SIZE
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
        val nzTypeToggleGroup = ToggleGroup().apply {
            selectedToggleProperty().addListener { observable, oldValue, newValue ->
                if (newValue is RadioButton) {
                    L.i(newValue.id)
                }
            }
        }
        companyNzTypeFlowPane?.children?.forEach {
            if (it is RadioButton) {
                it.toggleGroup = nzTypeToggleGroup
            }
        }
    }
}