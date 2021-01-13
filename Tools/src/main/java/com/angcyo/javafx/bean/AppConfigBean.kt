package com.angcyo.javafx.bean

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/12/29
 */
data class AppConfigBean(
    /**驱动程序的路径*/
    var driverPath: String? = null,

    /**输入历史*/
    var history: History? = null,

    /**主舞台是否置顶显示*/
    var isAlwaysOnTop: Boolean = false
)

fun AppConfigBean.history(): History {
    return this.run {
        if (history == null) {
            history = History()
        }
        history!!
    }
}

data class History(
    /**城市输入历史*/
    var cityList: List<String>? = null,

    /**行业输入历史*/
    var industryList: List<String>? = null,

    /**行业用语输入历史*/
    var termsWordList: List<String>? = null,

    var username: String? = null,
    var password: String? = null,
)
