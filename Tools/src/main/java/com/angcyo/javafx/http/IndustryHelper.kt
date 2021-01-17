package com.angcyo.javafx.http

import com.angcyo.http.base.fromJson
import com.angcyo.http.base.listType
import com.angcyo.javafx.base.ex.onBack
import com.angcyo.javafx.bean.IndustryBean
import com.angcyo.library.ex.*

/**
 * 行业用语 获取
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/01/15
 */
object IndustryHelper {

    //内资和秒批
    const val INDUSTRY_NZ_PATH = "./json/industry_nz.json"
    const val INDUSTRY_NZ_LIST_PATH = "./json/industry_nz_list.json"
    const val INDUSTRY_NZ_MP_PATH = "./json/industry_nz_mp.json"
    const val INDUSTRY_NZ_MP_LIST_PATH = "./json/industry_nz_mp_list.json"

    //个体户和秒批
    const val INDUSTRY_GTH_PATH = "./json/industry_gth.json"
    const val INDUSTRY_GTH_LIST_PATH = "./json/industry_gth_list.json"
    const val INDUSTRY_GTH_MP_PATH = "./json/industry_gth_mp.json"
    const val INDUSTRY_GTH_MP_LIST_PATH = "./json/industry_gth_mp_list.json"

    val nzIndustryFetch = NZIndustryFetch()
    val nzmpIndustryFetch = NZMPIndustryFetch()

    val gthIndustryFetch = GTHIndustryFetch()
    val gthmpIndustryFetch = GTHMPIndustryFetch()

    /**初始化行业用语*/
    fun init() {

        fun read(path: String, fetch: IndustryFetch) {
            val industryListJson = path.readText() ?: getString(path.subEnd("/", true)!!)
            industryListJson?.let {
                fetch.allChildList.resetAll(
                    it.fromJson<List<IndustryBean>>(listType(IndustryBean::class.java)) ?: emptyList()
                )
            }
        }

        read(INDUSTRY_NZ_LIST_PATH, nzIndustryFetch)
        read(INDUSTRY_NZ_MP_LIST_PATH, nzmpIndustryFetch)

        read(INDUSTRY_GTH_LIST_PATH, gthIndustryFetch)
        read(INDUSTRY_GTH_MP_LIST_PATH, gthmpIndustryFetch)
    }

    var startTime: Long = -1
    var endTime: Long = -1

    /**拉取所有分类的行业用语*/
    fun fetch(cookie: String) {
        startTime = nowTime()
        sync<Unit>(4) { countDownLatch, atomicReference ->
            onBack {
                nzIndustryFetch.fetch(cookie)
                countDownLatch.countDown()
            }
            onBack {
                nzmpIndustryFetch.fetch(cookie)
                countDownLatch.countDown()
            }
            onBack {
                gthIndustryFetch.fetch(cookie)
                countDownLatch.countDown()
            }
            onBack {
                gthmpIndustryFetch.fetch(cookie)
                countDownLatch.countDown()
            }
        }
        endTime = nowTime()
    }

    fun duration() = endTime - startTime
}