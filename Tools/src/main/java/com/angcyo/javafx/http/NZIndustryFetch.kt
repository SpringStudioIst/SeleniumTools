package com.angcyo.javafx.http

import com.angcyo.http.base.fromJson
import com.angcyo.http.base.getString
import com.angcyo.http.base.listType
import com.angcyo.http.request
import com.angcyo.javafx.bean.IndustryBean
import com.google.gson.JsonObject

/**
 * 内资行业用语拉取
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/01/17
 */
class NZIndustryFetch : IndustryFetch() {

    override fun fetch(cookie: String) {
        val rootList = fetchList(cookie, getUrlById("1"))
        updateAllList(rootList)
        saveList(IndustryHelper.INDUSTRY_NZ_PATH, IndustryHelper.INDUSTRY_NZ_LIST_PATH)
    }

    override fun getUrlById(id: String?): String {
        return "https://amr.sz.gov.cn/napout/command/dispatcher/com.inspur.gcloud.nameapproval.common.cmd.NameIndustryTreeCmd/getAsyncTreeData?tId=$id"
    }

    fun fetchRootList2(cookie: String): List<IndustryBean>? {
        var rootList: List<IndustryBean>? = null
        val bodyText =
            "{\"params\":{\"javaClass\":\"ParameterSet\",\"map\":{\"tId\":\"1\"},\"length\":1},\"context\":{\"javaClass\":\"HashMap\",\"map\":{},\"length\":0}}"
        request {
            url =
                "https://amr.sz.gov.cn/napout/command/ajax/com.inspur.gcloud.nameapproval.common.cmd.NameIndustryAjaxCmd/getAsyncTreeData"
            method = "post"
            body = jsonBody(bodyText)
            header = cookie.toHeader()
            async = false
            onEndAction = { response, exception ->
                response?.let {
                    val bodyString = it.body?.string()
                    bodyString?.let {
                        //val map = it.fromJson<Map<String, Any>>(mapType(String::class.java, Any::class.java))
                        val json = it.fromJson<JsonObject>(JsonObject::class.java)
                        //L.i(map)
                        val dataJson = (json?.get("map") as? JsonObject)?.getString("data")
                        rootList = dataJson.fromJson<List<IndustryBean>>(listType(IndustryBean::class.java))
                        //L.i(rootList)
                    }
                    //L.i(bodyString)
                }
            }
        }
        return rootList
    }
}