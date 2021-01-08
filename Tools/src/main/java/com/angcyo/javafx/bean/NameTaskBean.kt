package com.angcyo.javafx.bean

import com.angcyo.library.ex.nowTime
import com.angcyo.library.file.uuid

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/01/08
 */
data class NameTaskBean(
    var username: String? = null,
    var password: String? = null,
    var companyType: String? = null,
    var companyTypeName: String? = null,//中文显示
    var nzCompanyType: String? = null,
    var nzCompanyTypeName: String? = null,//中文显示
    var companyWord: String? = null,
    var termsWord: String? = null,

    /**唯一标识*/
    var uuid: String = uuid(),

    /**创建时间*/
    var createTime: Long = nowTime(),

    /**任务状态*/
    var taskState: Int = STATE_NORMAL,
) {
    companion object {
        //正常
        const val STATE_NORMAL = -1

        //运行中
        const val STATE_RUNNING = 1

        //完成
        const val STATE_FINISH = 0
    }
}