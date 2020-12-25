package com.angcyo.javafx.web

import org.openqa.selenium.TimeoutException
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.FluentWait
import java.time.Duration

/** 控制[org.openqa.selenium.WebElement]
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/12/24
 */
open class BaseControl(val driver: WebDriver) {

    companion object {
        const val TAGS = "span a i li p ul div"
    }

    /**先通过[Tag]获取标签, 再通过文本进行匹配元素*/
    fun findByText(text: String, tags: String = TAGS): List<WebElement> {
        return driver.findByText(text, tags)
    }

    /**通过标签[TAG]进行匹配元素*/
    fun findByTag(tags: String = TAGS, predicate: (WebElement) -> Boolean = { true }): List<WebElement> {
        return driver.findByTag(tags, predicate)
    }

    /**css选择器选择元素
     * document.querySelectorAll("input[placeholder=请输入账号]")
     * https://developer.mozilla.org/zh-CN/docs/Web/CSS/CSS_Selectors
     * */
    fun findByCss(css: String): List<WebElement> {
        return driver.findByCss(css)
    }

    /**查找链接<a>标签的文本*/
    fun findByLinkText(linkText: String): List<WebElement>? {
        return driver.findByLinkText(linkText)
    }

    /**等待网页指定约束准备就绪
     * [timeout] 等待超时,秒
     * [pollingEvery] 检查频率,秒
     *
     * [org.openqa.selenium.support.ui.FluentWait.timeoutException]
     * */
    @Throws(TimeoutException::class)
    fun waitBy(timeout: Long = 10, pollingEvery: Long = 1, action: () -> List<WebElement>?): List<WebElement>? {
        var result: List<WebElement>? = null
        FluentWait(driver)
            .withTimeout(Duration.ofSeconds(timeout))
            .pollingEvery(Duration.ofSeconds(pollingEvery))
            .until {
                result = action()
                !result.isNullOrEmpty()
            }
        return result
    }
}