package com.angcyo.library.ex

import com.angcyo.javafx.base.str
import java.net.URLDecoder
import java.net.URLEncoder
import java.util.regex.Pattern

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/12/20
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */

fun String.encode(enc: String = "UTF-8"): String = URLEncoder.encode(this, enc)

fun String.decode(enc: String = "UTF-8"): String = URLDecoder.decode(this, enc)

fun CharSequence?.or(default: CharSequence = "--") =
    if (this.isNullOrEmpty()) default else this

fun CharSequence?.orString(default: CharSequence = "--"): String =
    if (this.isNullOrEmpty()) default.toString() else this.toString()

fun CharSequence?.toString(): String = orString("")

/**将列表连成字符串*/
fun Iterable<*>.connect(
    divide: CharSequence = "," /*连接符*/,
    convert: (Any) -> CharSequence? = { it.toString() }
): String {
    return buildString {
        this@connect.forEach {
            it?.apply {
                val charSequence = convert(it)
                if (charSequence.isNullOrEmpty()) {

                } else {
                    append(charSequence).append(divide)
                }
            }
        }
        safe()
    }
}

fun Array<*>.connect(
    divide: CharSequence = "," /*连接符*/,
    convert: (Any) -> CharSequence? = { it.toString() }
): String {
    return buildString {
        this@connect.forEach {
            it?.apply {
                val charSequence = convert(it)
                if (charSequence.isNullOrEmpty()) {

                } else {
                    append(charSequence).append(divide)
                }
            }
        }
        safe()
    }
}

/**分割字符串*/
fun String?.splitList(
    separator: String = ",",
    allowEmpty: Boolean = false,
    checkExist: Boolean = false,
    maxCount: Int = Int.MAX_VALUE
): MutableList<String> {
    val result = mutableListOf<String>()

    if (this.isNullOrEmpty()) {
    } else if (this.toLowerCase() == "null") {
    } else if (separator.isNullOrEmpty()) {
    } else {
        for (s in this.split(separator.toRegex(), Int.MAX_VALUE)) {
            if (s.isNullOrEmpty() && !allowEmpty) {
                continue
            }
            if (result.contains(s) && checkExist) {
                continue
            }

            result.add(s)

            if (result.size >= maxCount) {
                break
            }
        }
    }

    return result
}

/** 安全的去掉字符串的最后一个字符 */
fun CharSequence.safe(): CharSequence? {
    return subSequence(0, kotlin.math.max(0, length - 1))
}

fun StringBuilder.safe(): StringBuilder {
    return delete(kotlin.math.max(0, lastIndex), kotlin.math.max(0, length))
}

/**判断字符串是否是纯数字, 支持正负整数,正负浮点数*/
fun String.isNumber(): Boolean {
    if (TextUtils.isEmpty(this)) {
        return false
    }
    val pattern = Pattern.compile("^[-\\+]?[\\d.]*\\d+$")
    return pattern.matcher(this).matches()
}

/**获取不带扩展名的文件名*/
fun String.noExtName(): String {
    if (this.isNotEmpty()) {
        val dot = lastIndexOf('.')
        if (dot > -1 && dot < length) {
            return substring(0, dot)
        }
    }
    return this
}

fun String?.isVideoMimeType(): Boolean {
    return this?.startsWith("video", true) ?: false
}

fun String?.isHttpMimeType(): Boolean {
    return this?.split("?")?.getOrNull(0)?.endsWith("html", true) ?: false
}

/**[android.media.MediaFile#isPlayListMimeType]*/
fun String?.isAudioMimeType(): Boolean {
    return this?.run {
        this.startsWith(
            "audio",
            true
        ) || this == "application/ogg"
    } ?: false
}

fun String?.isImageMimeType(): Boolean {
    return this?.startsWith("image", true) ?: false
}

fun String?.isTextMimeType(): Boolean {
    return this?.startsWith("text", true) ?: false
}

fun CharSequence?.patternList(
    regex: String?,
    orNoFind: String? = null /*未找到时, 默认*/
): MutableList<String> {
    return this.patternList(regex?.toPattern(), orNoFind)
}

/**获取字符串中所有匹配的数据(部分匹配), 更像是contains的关系*/
fun CharSequence?.patternList(
    pattern: Pattern?,
    orNoFind: String? = null /*未找到时, 默认*/
): MutableList<String> {
    val result = mutableListOf<String>()
    if (this == null) {
        return result
    }
    pattern?.let {
        val matcher = it.matcher(this)
        var isFind = false
        while (matcher.find()) {
            isFind = true
            result.add(matcher.group())
        }
        if (!isFind && orNoFind != null) {
            result.add(orNoFind)
        }
    }
    return result
}

fun CharSequence?.pattern(regex: String?, allowEmpty: Boolean = true): Boolean {
    return pattern(regex?.toPattern(), allowEmpty)
}

/**是否匹配成功(完成匹配)*/
fun CharSequence?.pattern(pattern: Pattern?, allowEmpty: Boolean = true): Boolean {

    if (TextUtils.isEmpty(this)) {
        if (allowEmpty) {
            return true
        }
    }

    if (this == null) {
        return false
    }
    if (pattern == null) {
        if (allowEmpty) {
            return true
        }
        return !TextUtils.isEmpty(this)
    }
    val matcher = pattern.matcher(this)
    return matcher.matches()
}

/**列表中的所有正则都匹配*/
fun CharSequence?.pattern(regexList: Iterable<String>, allowEmpty: Boolean = true): Boolean {
    if (TextUtils.isEmpty(this)) {
        if (allowEmpty) {
            return true
        }
    }

    if (this == null) {
        return false
    }

    if (!regexList.iterator().hasNext()) {
        if (allowEmpty) {
            return true
        }
        return !TextUtils.isEmpty(this)
    }

    var result = false

    // 有BUG的代码, if 条件永远不会为true, 虽然你已经给 result 赋值了true
    //    regexList.forEach {
    //        result = this.pattern(it, allowEmpty)
    //        if (result) {
    //            return@forEach
    //        }
    //    }

    regexList.forEach {
        //闭包外面声明的变量, 虽然已经修改了, 但是修改后的值, 不影响 if 条件判断
        if (this.pattern(it, allowEmpty)) {
            result = true
            return@forEach
        }
    }

    return result
}

/**将类名转换成Class*/
fun String.toClass(): Class<*>? {
    return try {
        Class.forName(this)
    } catch (e: Exception) {
        null
    }
}

/**host/url*/
fun String?.connectUrl(url: String?): String {
    val h = this?.trimEnd('/') ?: ""
    val u = url?.trimStart('/') ?: ""
    return "$h/$u"
}

fun String?.md5(): String? {
    return this?.toByteArray(Charsets.UTF_8)?.encrypt()?.toHexString()
}

/**[CharSequence]中是否包含指定[text]*/
fun CharSequence?.have(text: CharSequence): Boolean {
    if (this == null) {
        return false
    }
    return this.str() == text.str() || this.contains(text.toString().toRegex())
}

fun strEquals(a: CharSequence?, b: CharSequence?): Boolean {
    if (a === b) return true
    var length: Int = 0
    return if (a != null && b != null && a.length.also { length = it } == b.length) {
        if (a is String && b is String) {
            a == b
        } else {
            for (i in 0 until length) {
                if (a[i] != b[i]) return false
            }
            true
        }
    } else false
}