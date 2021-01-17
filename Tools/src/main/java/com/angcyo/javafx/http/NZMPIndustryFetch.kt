package com.angcyo.javafx.http

/**
 * 内资秒批行业用语拉取
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/01/17
 */
class NZMPIndustryFetch : IndustryFetch() {

    override fun fetch(cookie: String) {
        val rootList = fetchList(cookie, getUrlById("-1"))
        updateAllList(rootList)
        saveList(IndustryHelper.INDUSTRY_NZ_MP_PATH, IndustryHelper.INDUSTRY_NZ_MP_LIST_PATH)
    }

    override fun getUrlById(id: String?): String {
        return "https://amr.sz.gov.cn/napout/command/dispatcher/com.inspur.gcloud.nameapproval.core.cmd.DictTreeDispactherCmd/dictItemSelect?dictCode=NAP_INDUSTRY_FAST&parentCode=${id}&hideCheckBox=1"
    }
}