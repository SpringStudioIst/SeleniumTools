package com.angcyo.javafx.http

/**
 * 内资秒批行业用语拉取
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/01/17
 */
class GTHIndustryFetch : IndustryFetch() {

    override fun fetch(cookie: String) {
        val rootList = fetchList(cookie, getUrlById("1"))
        updateAllList(rootList)
        saveList(IndustryHelper.INDUSTRY_GTH_PATH, IndustryHelper.INDUSTRY_GTH_LIST_PATH)
    }

    override fun getUrlById(id: String?): String {
        return "https://amr.sz.gov.cn/iapout/command/dispatcher/com.inspur.gcloud.iap.common.cmd.NameIndustryTreeCmd/getAsyncTreeData?tId=${id}&treeCode=2"
    }
}