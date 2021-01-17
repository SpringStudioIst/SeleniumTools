package com.angcyo.javafx.http

import com.angcyo.core.component.file.writeTo
import com.angcyo.http.base.fromJson
import com.angcyo.http.base.listType
import com.angcyo.http.base.toJson
import com.angcyo.http.request
import com.angcyo.javafx.bean.IndustryBean
import com.angcyo.library.ex.file
import com.angcyo.log.L

/**
 * 行业用语获取
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/01/17
 */
abstract class IndustryFetch {

    var allList: List<IndustryBean>? = null
    var allChildList = mutableListOf<IndustryBean>()

    abstract fun fetch(cookie: String)

    abstract fun getUrlById(id: String?): String

    open fun fetchList(cookie: String, url: String): List<IndustryBean>? {
        var result: List<IndustryBean>? = null
        request {
            this.url = url
            method = "post"
            header = cookie.toHeader()
            async = false
            onEndAction = { response, exception ->
                response?.let {
                    val bodyString = it.body?.string()
                    bodyString?.let {
                        result = it.fromJson<List<IndustryBean>>(listType(IndustryBean::class.java))
                    }
                    result?.forEach { bean ->
                        if (bean.isParent?.toBoolean() == true) {
                            bean.childList = fetchList(cookie, getUrlById(bean.id))
                        }
                    }
                }
                exception?.let {
                    L.e("异常:$it")
                }
            }
        }
        return result
    }

    fun String.toHeader() = hashMapOf(
        "Cookie" to this,
        //"Host" to "amr.sz.gov.cn",
        "User-Agent" to "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36 Edg/87.0.664.66"
    )

    fun updateAllList(list: List<IndustryBean>?) {
        allList = list
        list?.forEach {
            updateChildList(it, allChildList)
        }
    }

    fun updateChildList(bean: IndustryBean, result: MutableList<IndustryBean>) {
        if (bean.isParent?.toBoolean() != true) {
            val find = result.find { it.id == bean.id }
            if (find == null) {
                result.add(bean)
            }
        } else {
            bean.childList?.forEach {
                updateChildList(it, result)
            }
        }
    }

    fun saveList(allPath: String, childPath: String) {
        allList.toJson()?.let {
            it.writeTo(allPath.file()!!, false)
        }
        allChildList.toJson()?.let {
            it.writeTo(childPath.file()!!, false)
        }
    }
}