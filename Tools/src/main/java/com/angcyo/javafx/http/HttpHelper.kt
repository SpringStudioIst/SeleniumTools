package com.angcyo.javafx.http

import com.angcyo.core.component.file.writeTo
import com.angcyo.http.base.fromJson
import com.angcyo.http.base.listType
import com.angcyo.http.base.toJson
import com.angcyo.http.post
import com.angcyo.http.rx.observe
import com.angcyo.http.toBean
import com.angcyo.javafx.bean.IndustryBean
import com.angcyo.library.ex.getString
import com.angcyo.library.ex.readText
import com.google.gson.JsonElement
import retrofit2.Response
import java.io.File

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/01/04
 */
object HttpHelper {

    const val INDUSTRY_PATH = "./json/industry.json"
    const val INDUSTRY_LIST_PATH = "./json/industry_list.json"

    /**根的分类*/
    val rootIndustry = IndustryBean(id = "-1", isParent = "true")

    /**所有可以选择的子项*/
    val industryList = mutableListOf<IndustryBean>()

    fun init() {
        val industryJson = INDUSTRY_PATH.readText() ?: getString("industry.json")
        industryJson?.let {
            rootIndustry.childList = it.fromJson<List<IndustryBean>>(listType(IndustryBean::class.java))
        }

        val industryListJson = INDUSTRY_LIST_PATH.readText() ?: getString("industry_list.json")
        industryListJson?.let {
            industryList.clear()
            industryList.addAll(it.fromJson<List<IndustryBean>>(listType(IndustryBean::class.java)) ?: emptyList())
        }
    }

    /**加载所有行业用语秒批*/
    fun loadIndustryList(
        cookie: String,
        end: (Response<JsonElement>?, Throwable?) -> Unit = { _, _ -> }
    ) {
        //获取根分类目录
        loadByIndustryBean(rootIndustry, cookie, end)
    }

    fun loadByIndustryBean(
        bean: IndustryBean,
        cookie: String,
        end: (Response<JsonElement>?, Throwable?) -> Unit = { _, _ -> }
    ) {
        if (bean.isParent == "true") {
            post {
                url =
                    "https://amr.sz.gov.cn/napout/command/dispatcher/com.inspur.gcloud.nameapproval.core.cmd.DictTreeDispactherCmd/dictItemSelect?dictCode=NAP_INDUSTRY_FAST&parentCode=${bean.id}&hideCheckBox=1"
                header = hashMapOf(
                    "Cookie" to cookie,
                    "Host" to "amr.sz.gov.cn",
                    "User-Agent" to "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36 Edg/87.0.664.66"
                )
            }.observe { data, error ->
                bean.childList = data?.toBean<List<IndustryBean>>(listType(IndustryBean::class.java))
                bean.childList?.forEach {
                    if (it.isParent == "true") {
                        loadByIndustryBean(it, cookie, end)
                    } else {
                        if (bean == rootIndustry) {
                            industryList.clear()
                        }
                        industryList.add(it)
                        rootIndustry.childList.toJson().writeTo(File(INDUSTRY_PATH), false)
                        industryList.toJson().writeTo(File(INDUSTRY_LIST_PATH), false)
                    }
                }
                end(data, error)
            }
        }
    }
}