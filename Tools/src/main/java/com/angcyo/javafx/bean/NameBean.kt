package com.angcyo.javafx.bean

/**
 * 核名配置的参数
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/01/04
 */
data class NameBean(
    var userame: String? = null,
    var password: String? = null,

    //企业类别
    var companyTypeCss: String? = null,

    //内资企业类别, 只有企业类别是`内资`时才有值
    var companyNzTypeCss: String? = null,
)
