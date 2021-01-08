package com.angcyo.javafx.bean

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/12/29
 */
data class AppConfigBean(
    /**驱动程序的路径*/
    var driverPath: String? = null,

    /**城市输入历史*/
    var cityHistoryList: List<String>? = null,

    /**行业输入历史*/
    var industryHistoryList: List<String>? = null,

    /**行业用语输入历史*/
    var termsWordHistoryList: List<String>? = null,
)
