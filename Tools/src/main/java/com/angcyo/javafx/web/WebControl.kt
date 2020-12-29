package com.angcyo.javafx.web

import com.angcyo.log.L
import com.angcyo.selenium.auto.BaseControl
import com.angcyo.selenium.clickSafe
import com.angcyo.selenium.sendKeysSafe
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.locators.RelativeLocator.withTagName
import kotlin.concurrent.thread

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/12/24
 */
class WebControl(driver: WebDriver) : BaseControl(driver) {

    var isRunning = false

    /**基础配置信息*/
    fun baseConfig() {
        //全屏浏览器
        driver.manage().window().maximize()
    }

    fun doAction() {
        if (isRunning) {
            L.e("已经在运行")
            return
        }
        thread {
            isRunning = true
            baseConfig()
            val url = "https://amr.sz.gov.cn/aicmerout/jsp/gcloud/giapout/industry/aicmer/processpage/step_prewin.jsp"
            driver.get(url)
            /*waitBy { findByLinkText("我要申办") }?.forEach {
                L.i(it.str())
            }*/
            // 点击我要申办
            waitBy { findByText("^我要申办$") }?.forEach {
                //L.i(it.text)
                it.clickSafe()
            }
            // 账号密码
            waitBy { findByText("^账号密码$") }?.forEach {
                //L.i(it.text)
                it.clickSafe()
            }
            waitBy { findByCss("input[placeholder=请输入账号]") }?.forEach {
                //L.i(it.text)
                //it.clickSafe()
                it.sendKeysSafe("angcyo")
            }
            waitBy { findByCss("input[placeholder=请输入密码]") }?.forEach {
                //L.i(it.text)
                //it.clickSafe()
                it.sendKeysSafe("!!angcyo521")
            }
            waitBy { findByCss("input[placeholder=请输入验证码]") }?.forEach { codeInput ->
                codeInput.findElement(withTagName("img").toRightOf(codeInput))?.let {
                    val base64Image = "data:image/jpeg;base64,"
                    val codeImage = it.getAttribute("src")?.run {
                        subSequence(base64Image.length, length)
                    }
                    codeImage?.let {
                        val code = Code.parseSync(it.toString())
                        //L.i(it.text)
                        //it.clickSafe()
                        codeInput.sendKeysSafe(code)
                    }
                }
            }
            waitBy { findByText("^登录$") }?.forEach {
                //L.i(it.text)
                it.clickSafe()
            }
            L.e("结束...")
            isRunning = false
        }
    }
}