package com.angcyo.javafx.web

import com.angcyo.javafx.base.L
import com.angcyo.javafx.base.have
import org.openqa.selenium.By
import org.openqa.selenium.Rectangle
import org.openqa.selenium.SearchContext
import org.openqa.selenium.WebElement
import org.openqa.selenium.remote.RemoteWebElement

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/12/24
 */

fun WebElement.toStr(): String {
    return buildString {
        try {
            append("标签:${tagName}")
            append(" 文本:${text}")
            append(" 位置:${location} 大小:${size} 矩形:${rect.toStr()}")
            append(" 显示:${isDisplayed}")
            if (this@toStr is RemoteWebElement) {
                appendln()
                //${coordinates.onScreen()}  Not supported yet.
                append("坐标 inViewPort:${coordinates.inViewPort()} onPage:${coordinates.onPage()} ${coordinates.auxiliary} json:${toJson()}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

fun WebElement.clickSafe(): Boolean {
    return try {
        click()
        true
    } catch (e: Exception) {
        L.e("异常:${this}")
        e.printStackTrace()
        false
    }
}

fun WebElement.sendKeysSafe(vararg keysToSend: CharSequence?): Boolean {
    return try {
        sendKeys(*keysToSend)
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

fun Rectangle.toStr(): String {
    return buildString {
        append("(")
        append("$x, $y")
        append(":")
        append("$width, $height")
        append(")")
    }
}

/**枚举查找[WebElement]by[TagName]*/
fun String.eachTagWebElement(context: SearchContext, action: WebElement.() -> Unit) {
    split(" ").forEach { tag ->
        context.findElements(By.tagName(tag))?.forEach { element ->
            element.action()
        }
    }
}

/**先通过[Tag]获取标签, 再通过文本进行匹配元素*/
fun SearchContext.findByText(text: String, tags: String = BaseControl.TAGS): List<WebElement> {
    val result = mutableListOf<WebElement>()
    tags.eachTagWebElement(this) {
        try {
            if (this.text.have(text)) {
                result.add(this)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    return result
}

/**通过标签[TAG]进行匹配元素*/
fun SearchContext.findByTag(
    tags: String = BaseControl.TAGS,
    predicate: (WebElement) -> Boolean = { true }
): List<WebElement> {
    val result = mutableListOf<WebElement>()
    tags.eachTagWebElement(this) {
        try {
            if (predicate(this)) {
                result.add(this)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    return result
}

/**css选择器选择元素
 * document.querySelectorAll("input[placeholder=请输入账号]")
 * https://developer.mozilla.org/zh-CN/docs/Web/CSS/CSS_Selectors
 * */
fun SearchContext.findByCss(css: String): List<WebElement> {
    return findElements(By.cssSelector(css))
}

/**查找链接<a>标签的文本*/
fun SearchContext.findByLinkText(linkText: String): List<WebElement>? {
    return findElements(By.partialLinkText(linkText))
}
