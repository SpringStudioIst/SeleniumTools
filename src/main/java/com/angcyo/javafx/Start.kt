package com.angcyo.javafx

/**
 * Email:angcyo@126.com
 *
 * @author angcyo
 * @date 2020/12/22
 */

class Start {
    companion object {
        /**
         * 需要使用java静态方法做一次跳转, 否则会提示
         * `缺少 JavaFX 运行时组件, 需要使用该组件来运行此应用程序`
         */
        @JvmStatic
        fun main(vararg args: String) {
            App.runApp(*args)
        }
    }
}

