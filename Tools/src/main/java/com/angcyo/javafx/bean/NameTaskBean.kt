package com.angcyo.javafx.bean

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
)