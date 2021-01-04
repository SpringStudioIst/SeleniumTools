package com.angcyo.javafx.bean

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/01/04
 */
data class IndustryBean(
    var id: String? = null,
    var isParent: String? = null,
    var name: String? = null,
    var nocheck: String? = null,
    var `open`: String? = null,
    var pId: String? = null,
    var childList: List<IndustryBean>? = null
)