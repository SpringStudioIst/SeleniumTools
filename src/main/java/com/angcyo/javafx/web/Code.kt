package com.angcyo.javafx.web

import com.angcyo.http.base.fromJson
import com.angcyo.http.base.getString
import com.angcyo.http.post
import com.angcyo.http.rx.observe
import com.angcyo.library.ex.md5
import com.angcyo.library.ex.nowTime
import com.google.gson.JsonElement
import java.util.concurrent.CountDownLatch

/** 验证码在线识别, 斐斐打码
 *
 * http://www.fateadm.com/index.html
 * http://docs.fateadm.com/web/#/1?page_id=6
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/12/25
 */
object Code {

    /**同步请求网络
     * http://docs.fateadm.com/web/#/1?page_id=6
     * */
    fun parseSync(imageData: String): String {
        val timestamp = nowTime() / 1000  //3555bbb397993599e2aa491dbec22bfa

        val pdId = 127454
        val pdKey = "U5JHM/comz/DsmBbmSYkwRFwf2eu+mFG"

        val pdMd5 = "$timestamp$pdKey".md5()?.toLowerCase()
        val sign = "$pdId$timestamp$pdMd5".md5()?.toLowerCase() ?: ""

        val latch = CountDownLatch(1)

        var result = ""
        post {
            url = "http://pred.fateadm.com/api/capreg"
            formMap = hashMapOf(
                "user_id" to 127454,
                "timestamp" to timestamp,
                "predict_type" to 30400,
                "img_data" to imageData,
                "sign" to sign
            )
            isSuccessful = {
                it.isSuccessful && it.body()?.getString("RetCode") == "0"
            }
        }.observe { data, error ->
            val body = data?.body()?.getString("RspData").fromJson<JsonElement>()?.getString("result")
            result = body ?: ""
            latch.countDown()
        }
        latch.await()
        return result
    }
}