package com.angcyo.javafx.http

import com.angcyo.core.component.file.writeTo
import com.angcyo.http.base.listType
import com.angcyo.http.base.toJson
import com.angcyo.http.post
import com.angcyo.http.rx.observe
import com.angcyo.http.toBean
import com.angcyo.javafx.bean.IndustryBean
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

    /**加载所有行业用语秒批*/
    fun loadIndustryList(cookie: String = "JSESSIONID=0000PFjxUQNB1aNCNLdskqv5F66:-1; __jsluid_s=82fbd04630601bfcba99591798872d0b; __jsluid_h=6462bcc9ea1e0c0eb86abd13cf58aab5; insert_cookie=13500475; sangfor_cookie=28150568; userType=1; ishelp=false; Hm_lvt_f89f708d1e989e02c93927bcee99fb29=1608290543,1608803383,1609744992; Hm_lpvt_f89f708d1e989e02c93927bcee99fb29=1609744992; swfUrl=%2Fztzl2020%2Fcnill_polyfill.swf; isCaUser=true; isSameUser=644BA689257D8F5CE20DC25BEE9BE39F; psout_sso_token=cOgCn5OrnuQobmNihcKZySh; gdbsTokenId=AQIC5wM2LY4Sfcxcz_aAHLFznG-6M6d_HmzOMca2csbRvKY.*AAJTSQACMDcAAlNLABQtODU5MTcwMzU0NTM5NTIwMzM1Mw..*@node2; accessToken=bd83601a-b325-4805-bd25-1f754b5454ef@node2; JSESSIONID=00002APouKoRTyHfa8AQ7aAkt_f:-1") {
        //获取根分类目录
        loadByIndustryBean(rootIndustry, cookie)
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
                        loadByIndustryBean(it, cookie)
                    } else {
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