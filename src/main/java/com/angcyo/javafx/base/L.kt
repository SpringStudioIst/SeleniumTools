package com.angcyo.javafx.base

import com.google.gson.JsonParser

/**
 * 日志输出类
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/12/30
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */

object L {
    val LINE_SEPARATOR = System.getProperty("line.separator")
    val ARRAY_SEPARATOR = ","

    const val VERBOSE = 2
    const val DEBUG = 3
    const val INFO = 4
    const val WARN = 5
    const val ERROR = 6

    val DEFAULT_LOG_PRING: (tag: String, level: Int, msg: String) -> Unit =
        { tag, level, msg ->
            when (level) {
                VERBOSE -> println("$tag $msg")
                DEBUG -> println("$tag $msg")
                INFO -> println("$tag $msg")
                WARN -> println("$tag $msg")
                ERROR -> System.err.println("$tag $msg")
            }
        }

    var debug = false

    var tag: String = "L"
        get() {
            return _tempTag ?: field
        }

    /**打印多少级的堆栈信息*/
    var stackTraceDepth: Int = 2
        get() = if (_tempStackTraceDepth > 0) _tempStackTraceDepth else field

    var _tempStackTraceDepth: Int = -1

    /**堆栈跳过前多少个*/
    var stackTraceFront: Int = 2
        get() = if (_tempStackTraceFront > 0) _tempStackTraceFront else field

    var _tempStackTraceFront: Int = -1

    /**Json缩进偏移量*/
    var indentJsonDepth: Int = 2

    var logPrint: (tag: String, level: Int, msg: String) -> Unit = DEFAULT_LOG_PRING

    //临时tag
    var _tempTag: String? = null

    //当前日志输出级别
    var _level: Int = DEBUG

    fun init(tag: String, debug: Boolean = false) {
        L.tag = tag
        L.debug = debug
    }

    fun v(vararg msg: Any?) {
        _level = VERBOSE
        _log(*msg)
    }

    fun d(vararg msg: Any?) {
        _level = DEBUG
        _log(*msg)
    }

    fun i(vararg msg: Any?) {
        _level = INFO
        _log(*msg)
    }

    fun w(vararg msg: Any?) {
        _level = WARN
        _log(*msg)
    }

    fun e(vararg msg: Any?) {
        _level = ERROR
        _log(*msg)
    }

    fun vt(tag: String, vararg msg: Any?) {
        _tempTag = tag
        _level = VERBOSE
        _log(*msg)
    }

    fun dt(tag: String, vararg msg: Any?) {
        _tempTag = tag
        _level = DEBUG
        _log(*msg)
    }

    fun it(tag: String, vararg msg: Any?) {
        _tempTag = tag
        _level = INFO
        _log(*msg)
    }

    fun wt(tag: String, vararg msg: Any?) {
        _tempTag = tag
        _level = WARN
        _log(*msg)
    }

    fun et(tag: String, vararg msg: Any?) {
        _tempTag = tag
        _level = ERROR
        _log(*msg)
    }

    val _stackContextBuilder = StringBuilder()
    val _logBuilder = StringBuilder()

    fun _log(vararg msg: Any?) {
        if (!debug) {
            return
        }

        _stackContextBuilder.clear()
        val stackTrace = getStackTrace(stackTraceFront, stackTraceDepth)
        val stackContext = _stackContextBuilder.apply {
            append("[")
            stackTrace.forEachIndexed { index, element ->
                append("(")
                append(element.fileName)
                if (index == stackTrace.lastIndex) {
                    append(":")
                    append(element.lineNumber)
                    append(")")
                }
                append("#")
                append(element.methodName)

                if (index == stackTrace.lastIndex) {
                    append(":")
                    append(Thread.currentThread().name)
                } else {
                    append("#")
                    append(element.lineNumber)
                    append(" ")
                }
            }
            append("]")
        }

        _logBuilder.clear()
        val logMsg = _logBuilder.apply {
            msg.forEach {
                when (it) {
                    is CharSequence -> append(_wrapJson("$it"))
                    is Iterable<*> -> {
                        append("[")
                        it.forEachIndexed { index, any ->
                            append(any.toString())
                            if (index != it.count() - 1) {
                                append(ARRAY_SEPARATOR)
                            }
                        }
                        append("]")
                    }
                    is Array<*> -> {
                        append("[")
                        it.forEachIndexed { index, any ->
                            append(any.toString())
                            if (index != it.count() - 1) {
                                append(ARRAY_SEPARATOR)
                            }
                        }
                        append("]")
                    }
                    is IntArray -> {
                        append("[")
                        it.forEachIndexed { index, any ->
                            append(any.toString())
                            if (index != it.count() - 1) {
                                append(ARRAY_SEPARATOR)
                            }
                        }
                        append("]")
                    }
                    is LongArray -> {
                        append("[")
                        it.forEachIndexed { index, any ->
                            append(any.toString())
                            if (index != it.count() - 1) {
                                append(ARRAY_SEPARATOR)
                            }
                        }
                        append("]")
                    }
                    is FloatArray -> {
                        append("[")
                        it.forEachIndexed { index, any ->
                            append(any.toString())
                            if (index != it.count() - 1) {
                                append(ARRAY_SEPARATOR)
                            }
                        }
                        append("]")
                    }
                    is DoubleArray -> {
                        append("[")
                        it.forEachIndexed { index, any ->
                            append(any.toString())
                            if (index != it.count() - 1) {
                                append(ARRAY_SEPARATOR)
                            }
                        }
                        append("]")
                    }
                    else -> append(it.toString())
                }
            }
        }

        logPrint(tag, _level, "$stackContext $logMsg")

        _tempTag = null
        _tempStackTraceDepth = -1
        _tempStackTraceFront = -1
    }

    fun _wrapJson(msg: String): String {
        if (indentJsonDepth <= 0) {
            return msg
        }
        try {
            if (msg.startsWith("{") && msg.endsWith("}")) {
                val jsonObject = JsonParser().parse(msg)
                return LINE_SEPARATOR + jsonObject.toString()
            } else if (msg.startsWith("[") && msg.endsWith("]")) {
                val jsonArray = JsonParser().parse(msg)
                return LINE_SEPARATOR + jsonArray.toString()
            }
        } catch (e: Exception) {

        }
        return msg
    }
}

/**
 * 获取调用栈信息
 * [front] 当前调用位置的前几个开始
 * [count] 共几个, 负数表示全部
 * */
fun getStackTrace(front: Int = 0, count: Int = -1): List<StackTraceElement> {
    val stackTrace = Thread.currentThread().stackTrace
    stackTrace.reverse()
    val endIndex = stackTrace.size - 3 - front
    val startIndex = if (count > 0) (endIndex - count) else 0
    val slice = stackTrace.slice(startIndex until endIndex)
    return slice
}
